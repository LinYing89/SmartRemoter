package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.loop.LoopDuration
import java.util.ArrayList

class LoopDurationDao(context: Context) {
    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: LoopDurationDao? = null

        fun get(context: Context): LoopDurationDao {
            if (null == dao) {
                dao = LoopDurationDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(loopDuration: LoopDuration, zLoopId: String?): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabLoopDuration.Cols.ID, loopDuration.id)
        if (null != zLoopId) {
            values.put(DbSb.TabLoopDuration.Cols.ZLOOP_ID, zLoopId)
        }
        values.put(DbSb.TabLoopDuration.Cols.DELETED, loopDuration.isDeleted)
        return values
    }

    fun add(loopDuration: LoopDuration, zLoopId: String) {
        val list = find(DbSb.TabLoopDuration.Cols.ID + " = ?", arrayOf(loopDuration.id))
        if (list.size > 0) {
            update(loopDuration, zLoopId)
        } else {
            val values1 = getContentValues(loopDuration, zLoopId)
            mDatabase.insert(DbSb.TabLoopDuration.NAME, null, values1)
        }
    }

    fun delete(loopDuration: LoopDuration) {
        val myTimeDao = MyTimeDao.get(mContext)
        for (myTime in loopDuration.listTimes) {
            myTimeDao.delete(myTime)
        }
        mDatabase.delete(DbSb.TabLoopDuration.NAME, DbSb.TabLoopDuration.Cols.ID + "=?", arrayOf(loopDuration.id))
    }

    fun update(loopDuration: LoopDuration, zLoopId: String) {
        val values = getContentValues(loopDuration, zLoopId)
        mDatabase.update(DbSb.TabLoopDuration.NAME, values,
                "id = ?",
                arrayOf(loopDuration.id))
    }

    private fun find(whereClause: String, whereArgs: Array<String>): List<LoopDuration> {
        val cursor = query(whereClause, whereArgs)
        return createLoopDuration(cursor)
    }

    fun findById(zLoopId: String): List<LoopDuration> {
        val whereClause = DbSb.TabLoopDuration.Cols.ZLOOP_ID + " = ?"
        val whereArgs = arrayOf(zLoopId)
        val cursor = query(whereClause, whereArgs)
        return createLoopDuration(cursor)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): LoopDurationWrapper {
        val cursor = mDatabase.query(
                DbSb.TabLoopDuration.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return LoopDurationWrapper(cursor)
    }

    private fun createLoopDuration(cursor: LoopDurationWrapper): List<LoopDuration> {
        val loopDurations = ArrayList<LoopDuration>()
        try {
            cursor.moveToFirst()
            val myTimeDao = MyTimeDao.get(mContext)
            while (!cursor.isAfterLast()) {
                val loopDuration = cursor.getLoopDuration(mContext)
                loopDurations.add(loopDuration)
                loopDuration.setListTimes(myTimeDao.findByTimerId(loopDuration.getId()))
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return loopDurations
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabLoopDuration.NAME)
    }
}