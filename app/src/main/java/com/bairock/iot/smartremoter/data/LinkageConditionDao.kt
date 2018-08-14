package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.LinkageCondition
import java.util.ArrayList

class LinkageConditionDao(context: Context) {
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: LinkageConditionDao? = null

        fun get(context: Context): LinkageConditionDao {
            if (null == dao) {
                dao = LinkageConditionDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(linkageCondition: LinkageCondition, linkageId: String?): ContentValues {
        val values = ContentValues()
        if (null != linkageId) {
            values.put(DbSb.TabLinkageCondition.Cols.SUBCHAIN_ID, linkageId)
        }
        values.put(DbSb.TabLinkageCondition.Cols.ID, linkageCondition.id)
        values.put(DbSb.TabLinkageCondition.Cols.COMPARE_SYMBOL, linkageCondition.compareSymbol.toString())
        values.put(DbSb.TabLinkageCondition.Cols.COMPARE_VALUE, linkageCondition.compareValue)
        values.put(DbSb.TabLinkageCondition.Cols.LOGIC, linkageCondition.logic.toString())
        values.put(DbSb.TabLinkageCondition.Cols.TRIGGER_STYLE, linkageCondition.triggerStyle.toString())
        values.put(DbSb.TabLinkageCondition.Cols.DEV_ID, linkageCondition.device.id)
        values.put(DbSb.TabLinkageCondition.Cols.DELETED, linkageCondition.isDeleted)
        return values
    }

    fun add(linkageCondition: LinkageCondition, linkageId: String) {
        val list = find(DbSb.TabLinkageCondition.Cols.ID + " = ?", arrayOf(linkageCondition.id))
        if (list.size > 0) {
            update(linkageCondition, linkageId)
        } else {
            val values1 = getContentValues(linkageCondition, linkageId)
            mDatabase.insert(DbSb.TabLinkageCondition.NAME, null, values1)
        }
    }

    fun delete(linkageCondition: LinkageCondition) {
        deleteById(linkageCondition.id)
        //mDatabase.delete(DbSb.TabLinkageCondition.NAME, DbSb.TabLinkageCondition.Cols.ID + "=?", new String[]{linkageCondition.getId()});
    }

    fun deleteById(id: String) {
        mDatabase.delete(DbSb.TabLinkageCondition.NAME, DbSb.TabLinkageCondition.Cols.ID + "=?", arrayOf(id))
    }

    fun update(linkageCondition: LinkageCondition, linkageId: String) {
        val values = getContentValues(linkageCondition, linkageId)
        mDatabase.update(DbSb.TabLinkageCondition.NAME, values,
                "id = ?",
                arrayOf(linkageCondition.id))
    }

    private fun find(whereClause: String, whereArgs: Array<String>): List<LinkageCondition> {
        val cursor = query(whereClause, whereArgs)
        return createLinkageCondition(cursor)
    }

    fun findByLinkageId(linkageId: String): List<LinkageCondition> {
        val whereClause = DbSb.TabLinkageCondition.Cols.SUBCHAIN_ID + " = ?"
        val whereArgs = arrayOf(linkageId)
        val cursor = query(whereClause, whereArgs)
        return createLinkageCondition(cursor)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): LinkageConditionWrapper {
        val cursor = mDatabase.query(
                DbSb.TabLinkageCondition.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return LinkageConditionWrapper(cursor)
    }

    private fun createLinkageCondition(cursor: LinkageConditionWrapper): List<LinkageCondition> {
        val listLinkageCondition = ArrayList<LinkageCondition>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast()) {
                val linkageCondition = cursor.getLinkageCondition()
                if (null != linkageCondition) {
                    listLinkageCondition.add(linkageCondition)
                }
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listLinkageCondition
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabLinkageCondition.NAME)
    }
}