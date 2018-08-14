package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.timing.MyTime

class MyTimeWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getMyTime(): MyTime {
        val id = getString(getColumnIndex(DbSb.TabMyTime.Cols.ID))
        val hour = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.HOUR)))
        val minute = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.MINUTE)))
        val second = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.SECOND)))
        val type = Integer.parseInt(getString(getColumnIndex(DbSb.TabMyTime.Cols.TYPE)))
        val zLoop = MyTime()
        zLoop.id = id
        zLoop.type = type
        zLoop.hour = hour
        zLoop.minute = minute
        zLoop.second = second
        return zLoop
    }
}