package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.timing.WeekHelper

class WeekHelperWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getWeekHelper(): WeekHelper {
        val id = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.ID))
        val sun = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.SUN)) == "1"
        val mon = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.MON)) == "1"
        val tues = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.TUES)) == "1"
        val wed = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.WED)) == "1"
        val thur = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.THUR)) == "1"
        val fri = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.FRI)) == "1"
        val sat = getString(getColumnIndex(DbSb.TabWeekHelper.Cols.SAT)) == "1"
        val weekHelper = WeekHelper()
        weekHelper.id = id
        weekHelper.isSun = sun
        weekHelper.isMon = mon
        weekHelper.isTues = tues
        weekHelper.isWed = wed
        weekHelper.isThur = thur
        weekHelper.isFri = fri
        weekHelper.isSat = sat
        return weekHelper
    }
}