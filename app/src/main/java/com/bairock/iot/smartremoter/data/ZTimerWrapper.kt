package com.bairock.iot.smartremoter.data

import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.timing.ZTimer

class ZTimerWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getZTimer(): ZTimer {
        //String linkageHolderId = getString(getColumnIndex(DbSb.TabZLoop.Cols.LINKAGE_HOLDER_ID));
        val id = getString(getColumnIndex(DbSb.TabZTimer.Cols.ID))
        val enable = getString(getColumnIndex(DbSb.TabZTimer.Cols.ENABLE)) == "1"
        val deleted = getString(getColumnIndex(DbSb.TabZTimer.Cols.DELETED)) == "1"
        val zTimer = ZTimer()
        zTimer.id = id
        zTimer.isEnable = enable
        zTimer.isDeleted = deleted
        return zTimer
    }
}