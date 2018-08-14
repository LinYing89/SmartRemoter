package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.timing.WeekHelper
import java.util.ArrayList

class WeekHelperDao(context: Context) {
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: WeekHelperDao? = null

        fun get(context: Context): WeekHelperDao {
            if (null == dao) {
                dao = WeekHelperDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(weekHelper: WeekHelper): ContentValues {
        val values = ContentValues()
        if (weekHelper.getzTimer() != null) {
            values.put(DbSb.TabWeekHelper.Cols.ZTIMER_ID, weekHelper.getzTimer().id)
        }
        values.put(DbSb.TabWeekHelper.Cols.ID, weekHelper.id)
        values.put(DbSb.TabWeekHelper.Cols.SUN, weekHelper.isSun)
        values.put(DbSb.TabWeekHelper.Cols.MON, weekHelper.isMon)
        values.put(DbSb.TabWeekHelper.Cols.TUES, weekHelper.isTues)
        values.put(DbSb.TabWeekHelper.Cols.WED, weekHelper.isWed)
        values.put(DbSb.TabWeekHelper.Cols.THUR, weekHelper.isThur)
        values.put(DbSb.TabWeekHelper.Cols.FRI, weekHelper.isFri)
        values.put(DbSb.TabWeekHelper.Cols.SAT, weekHelper.isSat)
        return values
    }

    fun add(weekHelper: WeekHelper) {
        val list = find(DbSb.TabWeekHelper.Cols.ID + " = ?", arrayOf(weekHelper.id))
        if (list.size > 0) {
            update(weekHelper)
        } else {
            val values1 = getContentValues(weekHelper)
            mDatabase.insert(DbSb.TabWeekHelper.NAME, null, values1)
        }
    }

    fun delete(weekHelper: WeekHelper) {
        mDatabase.delete(DbSb.TabWeekHelper.NAME, DbSb.TabWeekHelper.Cols.ID + "=?", arrayOf(weekHelper.id))
    }

    fun update(weekHelper: WeekHelper) {
        val values = getContentValues(weekHelper)
        mDatabase.update(DbSb.TabWeekHelper.NAME, values,
                "id = ?",
                arrayOf(weekHelper.id))
    }

    private fun find(whereClause: String, whereArgs: Array<String>): List<WeekHelper> {
        val cursor = query(whereClause, whereArgs)
        return createWeekHelper(cursor)
    }

    fun findById(id: String): WeekHelper? {
        val whereClause = DbSb.TabWeekHelper.Cols.ID + " = ?"
        val whereArgs = arrayOf(id)
        val cursor = query(whereClause, whereArgs)
        val list = createWeekHelper(cursor)
        return if (list.size > 0) {
            list[0]
        } else null
    }

    fun findByZTimerId(zTimerId: String): WeekHelper? {
        val whereClause = DbSb.TabWeekHelper.Cols.ZTIMER_ID + " = ?"
        val whereArgs = arrayOf(zTimerId)
        val cursor = query(whereClause, whereArgs)
        val list = createWeekHelper(cursor)
        return if (list.size > 0) {
            list[0]
        } else null
    }

    private fun query(whereClause: String, whereArgs: Array<String>): WeekHelperWrapper {
        val cursor = mDatabase.query(
                DbSb.TabWeekHelper.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return WeekHelperWrapper(cursor)
    }

    private fun createWeekHelper(cursor: WeekHelperWrapper): List<WeekHelper> {
        val weekHelpers = ArrayList<WeekHelper>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast()) {
                val weekHelper = cursor.getWeekHelper()
                weekHelpers.add(weekHelper)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return weekHelpers
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabWeekHelper.NAME)
    }
}