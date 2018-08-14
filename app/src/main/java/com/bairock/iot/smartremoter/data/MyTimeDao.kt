package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.timing.MyTime
import java.util.ArrayList

class MyTimeDao(context: Context) {
    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: MyTimeDao? = null

        fun get(context: Context): MyTimeDao {
            if (null == dao) {
                dao = MyTimeDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(myTime: MyTime, timerId: String?): ContentValues {
        val values = ContentValues()

        if (null != timerId) {
            values.put(DbSb.TabMyTime.Cols.TIMER_ID, timerId)
        }
        values.put(DbSb.TabMyTime.Cols.TYPE, myTime.type)
        values.put(DbSb.TabMyTime.Cols.ID, myTime.id)
        values.put(DbSb.TabMyTime.Cols.HOUR, myTime.hour)
        values.put(DbSb.TabMyTime.Cols.MINUTE, myTime.minute)
        values.put(DbSb.TabMyTime.Cols.SECOND, myTime.second)
        return values
    }

    fun add(myTime: MyTime, timerId: String) {
        val list = find(DbSb.TabMyTime.Cols.ID + " = ?", arrayOf(myTime.id))
        if (list.size > 0) {
            update(myTime, timerId)
        } else {
            val values1 = getContentValues(myTime, timerId)
            mDatabase.insert(DbSb.TabMyTime.NAME, null, values1)
        }
    }

    fun delete(myTime: MyTime) {
        mDatabase.delete(DbSb.TabMyTime.NAME, DbSb.TabMyTime.Cols.ID + "=?", arrayOf(myTime.id))
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabMyTime.NAME)
    }

    fun update(myTime: MyTime, timerId: String) {
        val values = getContentValues(myTime, timerId)
        mDatabase.update(DbSb.TabMyTime.NAME, values,
                "id = ?",
                arrayOf(myTime.id))
    }

    private fun find(whereClause: String, whereArgs: Array<String>): List<MyTime> {
        val cursor = query(whereClause, whereArgs)
        return createMyTime(cursor)
    }

    fun findById(myTimeId: String): MyTime {
        val whereClause = DbSb.TabMyTime.Cols.ID + " = ?"
        val whereArgs = arrayOf(myTimeId)
        val cursor = query(whereClause, whereArgs)
        val list = createMyTime(cursor)
        return if (list.size > 0) {
            list[0]
        } else {
            MyTime()
        }
    }

    fun findByTimerId(timerId: String): List<MyTime> {
        val whereClause = DbSb.TabMyTime.Cols.TIMER_ID + " = ?"
        val whereArgs = arrayOf(timerId)
        val cursor = query(whereClause, whereArgs)
        return createMyTime(cursor)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): MyTimeWrapper {
        val cursor = mDatabase.query(
                DbSb.TabMyTime.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return MyTimeWrapper(cursor)
    }

    private fun createMyTime(cursor: MyTimeWrapper): List<MyTime> {
        val zLoops = ArrayList<MyTime>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast()) {
                val myTime = cursor.getMyTime()
                zLoops.add(myTime)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return zLoops
    }
}