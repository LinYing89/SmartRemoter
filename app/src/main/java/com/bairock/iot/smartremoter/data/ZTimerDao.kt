package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.timing.ZTimer
import java.util.ArrayList

class ZTimerDao(context: Context) {

    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: ZTimerDao? = null

        fun get(context: Context): ZTimerDao {
            if (null == dao) {
                dao = ZTimerDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(zTimer: ZTimer, linkageHolderId: String?): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabZTimer.Cols.ID, zTimer.id)
        if (null != linkageHolderId) {
            values.put(DbSb.TabZTimer.Cols.TIMING_ID, linkageHolderId)
        }
        values.put(DbSb.TabZTimer.Cols.ENABLE, zTimer.isEnable)
        values.put(DbSb.TabZTimer.Cols.DELETED, zTimer.isDeleted)
        return values
    }

    fun add(zTimer: ZTimer, linkageHolderId: String) {
        val list = find(DbSb.TabZTimer.Cols.ID + " = ?", arrayOf(zTimer.id))
        if (list.size > 0) {
            update(zTimer, linkageHolderId)
        } else {
            val values1 = getContentValues(zTimer, linkageHolderId)
            mDatabase.insert(DbSb.TabZTimer.NAME, null, values1)
        }
    }

    fun delete(zTimer: ZTimer) {
        val myTimeDao = MyTimeDao.get(mContext)
        for (myTime in zTimer.listTimes) {
            myTimeDao.delete(myTime)
        }
        WeekHelperDao.get(mContext).delete(zTimer.weekHelper)

        mDatabase.delete(DbSb.TabZTimer.NAME, DbSb.TabZTimer.Cols.ID + "=?", arrayOf(zTimer.id))
    }

    fun update(zTimer: ZTimer, linkageHolderId: String) {
        val values = getContentValues(zTimer, linkageHolderId)
        mDatabase.update(DbSb.TabZTimer.NAME, values,
                "id = ?",
                arrayOf(zTimer.id))
    }

    private fun find(whereClause: String, whereArgs: Array<String>): List<ZTimer> {
        val cursor = query(whereClause, whereArgs)
        return createZTimer(cursor)
    }

    fun findByLinkageId(linkageId: String): List<ZTimer> {
        val whereClause = DbSb.TabZTimer.Cols.TIMING_ID + " = ?"
        val whereArgs = arrayOf(linkageId)
        val cursor = query(whereClause, whereArgs)
        return createZTimer(cursor)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): ZTimerWrapper {
        val cursor = mDatabase.query(
                DbSb.TabZTimer.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return ZTimerWrapper(cursor)
    }

    private fun createZTimer(cursor: ZTimerWrapper): List<ZTimer> {
        val zTimers = ArrayList<ZTimer>()
        try {
            cursor.moveToFirst()
            val myTimeDao = MyTimeDao.get(mContext)
            val weekHelperDao = WeekHelperDao.get(mContext)
            while (!cursor.isAfterLast()) {
                val zTimer = cursor.getZTimer()
                zTimers.add(zTimer)
                zTimer.setListTimes(myTimeDao.findByTimerId(zTimer.getId()))
                zTimer.setWeekHelper(weekHelperDao.findByZTimerId(zTimer.getId()))
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return zTimers
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabZTimer.NAME)
    }
}