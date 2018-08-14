package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.Linkage
import com.bairock.iot.intelDev.linkage.SubChain
import com.bairock.iot.intelDev.linkage.loop.ZLoop
import com.bairock.iot.intelDev.linkage.timing.Timing
import java.util.ArrayList

class LinkageDao(context: Context) {
    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: LinkageDao? = null

        fun get(context: Context): LinkageDao {
            if (null == dao) {
                dao = LinkageDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(linkage: Linkage, linkageHolderId: String?): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabLinkage.Cols.ID, linkage.id)
        if (null != linkageHolderId) {
            values.put(DbSb.TabLinkage.Cols.LINKAGE_HOLDER_ID, linkageHolderId)
        }
        values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, linkage.javaClass.simpleName)
        values.put(DbSb.TabLinkage.Cols.NAME, linkage.name)
        values.put(DbSb.TabLinkage.Cols.ENABLE, linkage.isEnable)
        values.put(DbSb.TabLinkage.Cols.DELETED, linkage.isDeleted)
        if (linkage is SubChain) {
            values.put(DbSb.TabLinkage.Cols.TRIGGERED, linkage.isTriggered)
            if (linkage is ZLoop) {
                values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, ZLoop::class.java.simpleName)
                values.put(DbSb.TabLinkage.Cols.LOOP_COUNT, linkage.loopCount)
            } else {
                values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, SubChain::class.java.simpleName)
            }
        } else if (linkage is Timing) {
            values.put(DbSb.TabLinkage.Cols.LINKAGE_TYPE, Timing::class.java.simpleName)
        }
        return values
    }

    private fun getUpdateIdContentValues(newId: String): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabLinkage.Cols.ID, newId)
        return values
    }

    fun add(linkage: Linkage, linkageHolderId: String) {
        val list = find(DbSb.TabLinkage.Cols.ID + " = ?", arrayOf(linkage.id))
        if (list.size > 0) {
            update(linkage, linkageHolderId)
        } else {
            val values1 = getContentValues(linkage, linkageHolderId)
            mDatabase.insert(DbSb.TabLinkage.NAME, null, values1)
        }
    }

    fun delete(linkage: Linkage) {
        if (linkage is SubChain) {
            val linkageConditionDao = LinkageConditionDao.get(mContext)
            for (linkageCondition in linkage.listCondition) {
                linkageConditionDao.delete(linkageCondition)
            }
            if (linkage is ZLoop) {
                val loopDurationDao = LoopDurationDao.get(mContext)
                for (loopDuration in linkage.listLoopDuration) {
                    loopDurationDao.delete(loopDuration)
                }
            }
        } else if (linkage is Timing) {
            val zTimerDao = ZTimerDao.get(mContext)
            for (zTimer in linkage.listZTimer) {
                zTimerDao.delete(zTimer)
            }
        }

        val effectDao = EffectDao.get(mContext)
        for (effect in linkage.listEffect) {
            effectDao.delete(effect)
        }

        mDatabase.delete(DbSb.TabLinkage.NAME, DbSb.TabLinkage.Cols.ID + "=?", arrayOf(linkage.id))
    }

    fun update(linkage: Linkage, linkageHolderId: String) {
        val values = getContentValues(linkage, linkageHolderId)
        mDatabase.update(DbSb.TabLinkage.NAME, values,
                "id = ?",
                arrayOf(linkage.id))
    }

    fun updateId(oldId: String, newId: String) {
        val values = getUpdateIdContentValues(newId)
        mDatabase.update(DbSb.TabLinkage.NAME, values,
                "id = ?",
                arrayOf(oldId))
    }

    fun find(whereClause: String, whereArgs: Array<String>): List<Linkage> {
        val cursor = query(whereClause, whereArgs)
        return createLinkageDevValue(cursor, true)
    }

    fun findChainByLinkageHolderId(linkageHolderId: String): List<Linkage> {
        val whereClause = DbSb.TabLinkage.Cols.LINKAGE_HOLDER_ID + " = ?"
        val whereArgs = arrayOf(linkageHolderId)
        val cursor = query(whereClause, whereArgs)
        return createLinkageDevValue(cursor, false)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): LinkageWrapper {
        val cursor = mDatabase.query(
                DbSb.TabLinkage.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return LinkageWrapper(cursor)
    }

    private fun createLinkageDevValue(cursor: LinkageWrapper, lazy: Boolean): List<Linkage> {
        val linkages = ArrayList<Linkage>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast()) {
                val linkage = cursor.getLinkage()
                linkages.add(linkage)
                if (!lazy) {
                    initLinkage(linkage)
                }
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return linkages
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabLinkage.NAME)
    }

    private fun initLinkage(linkage: Linkage) {
        if (linkage is SubChain) {
            val linkageConditionDao = LinkageConditionDao.get(mContext)
            val linkageConditions = linkageConditionDao.findByLinkageId(linkage.getId())
            linkage.listCondition = linkageConditions
            if (linkage is ZLoop) {
                val loopDurationDao = LoopDurationDao.get(mContext)
                val loopDurations = loopDurationDao.findById(linkage.getId())
                linkage.listLoopDuration = loopDurations
            }
        } else if (linkage is Timing) {
            val zTimerDao = ZTimerDao.get(mContext)
            val list = zTimerDao.findByLinkageId(linkage.getId())
            linkage.listZTimer = list
        }
        val effectDao = EffectDao.get(mContext)
        linkage.listEffect = effectDao.findByLinkageId(linkage.id)
    }
}