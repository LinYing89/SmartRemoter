package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.ChainHolder
import com.bairock.iot.intelDev.linkage.LinkageHolder
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder
import com.bairock.iot.intelDev.linkage.loop.LoopHolder
import com.bairock.iot.intelDev.linkage.timing.TimingHolder
import java.util.ArrayList

class LinkageHolderDao(context: Context) {

    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: LinkageHolderDao? = null

        fun get(context: Context): LinkageHolderDao {
            if (null == dao) {
                dao = LinkageHolderDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(linkageHolder: LinkageHolder): ContentValues {
        val values = ContentValues()
        if (null != linkageHolder.devGroup) {
            values.put(DbSb.TabLinkageHolder.Cols.DEVGROUP_ID, linkageHolder.devGroup.id)
        }
        values.put(DbSb.TabLinkageHolder.Cols.ID, linkageHolder.id)
        values.put(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE, linkageHolder.javaClass.simpleName)
        values.put(DbSb.TabLinkageHolder.Cols.ENABLE, linkageHolder.isEnable)
        return values
    }

    private fun getUpdateIdContentValues(newId: String): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabLinkageHolder.Cols.ID, newId)
        return values
    }

    fun add(linkageHolder: LinkageHolder) {
        val list = find(DbSb.TabLinkageHolder.Cols.ID + " = ?", arrayOf(linkageHolder.id))
        if (list.size > 0) {
            update(linkageHolder)
        } else {
            val values1 = getContentValues(linkageHolder)
            mDatabase.insert(DbSb.TabLinkageHolder.NAME, null, values1)
        }
    }

    fun update(linkageHolder: LinkageHolder) {
        val values = getContentValues(linkageHolder)
        mDatabase.update(DbSb.TabLinkageHolder.NAME, values,
                "id = ?",
                arrayOf(linkageHolder.id))
    }

    fun updateId(oldId: String, newId: String) {
        val values = getUpdateIdContentValues(newId)
        mDatabase.update(DbSb.TabLinkageHolder.NAME, values,
                "id = ?",
                arrayOf(oldId))
    }

    fun find(): List<LinkageHolder> {
        val cursor = query()
        return createLinkageHolder(cursor)
    }

    fun find(whereClause: String, whereArgs: Array<String>): List<LinkageHolder> {
        val cursor = query(whereClause, whereArgs)
        return createLinkageHolder(cursor)
    }

    fun findByDevGroupId(devGroupId: Long): List<LinkageHolder> {
        val cursor = query(DbSb.TabLinkageHolder.Cols.DEVGROUP_ID + " =?", arrayOf(devGroupId.toString()))
        return createLinkageHolder(cursor)
    }

    fun findChainHolder(): ChainHolder? {
        val cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", arrayOf(ChainHolder::class.java.simpleName))
        val list = createLinkageHolder(cursor)
        return if (list.size > 0) {
            list[0] as ChainHolder
        } else null
    }

    fun findLoopHolder(): LoopHolder? {
        val cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", arrayOf(LoopHolder::class.java.simpleName))
        val list = createLinkageHolder(cursor)
        return if (list.size > 0) {
            list[0] as LoopHolder
        } else null
    }

    fun findTimingHolder(): TimingHolder? {
        val cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", arrayOf(TimingHolder::class.java.simpleName))
        val list = createLinkageHolder(cursor)
        return if (list.size > 0) {
            list[0] as TimingHolder
        } else null
    }

    fun findGuaguaHolder(): GuaguaHolder? {
        val cursor = query(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE + " =?", arrayOf(GuaguaHolder::class.java.simpleName))
        val list = createLinkageHolder(cursor)
        return if (list.size > 0) {
            list[0] as GuaguaHolder
        } else null
    }

    private fun query(): LinkageHolderWrapper {
        val cursor = mDatabase.query(
                DbSb.TabLinkageHolder.NAME, // having
                null // orderBy
                , null, null, null, null, null
        )// Columns - null selects all columns
        // groupBy
        return LinkageHolderWrapper(cursor)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): LinkageHolderWrapper {
        val cursor = mDatabase.query(
                DbSb.TabLinkageHolder.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return LinkageHolderWrapper(cursor)
    }

    private fun createLinkageHolder(cursor: LinkageHolderWrapper): List<LinkageHolder> {
        val listLinkageHolder = ArrayList<LinkageHolder>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast()) {
                val linkageHolder = cursor.getLinkageHolder()
                listLinkageHolder.add(linkageHolder)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listLinkageHolder
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabLinkageHolder.NAME)
    }
}