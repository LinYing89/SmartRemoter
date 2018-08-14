package com.bairock.iot.smartremoter.data

import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.loop.LoopDuration

class LoopDurationWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getLoopDuration(context: Context): LoopDuration {
        //String linkageHolderId = getString(getColumnIndex(DbSb.TabZLoop.Cols.LINKAGE_HOLDER_ID));
        val id = getString(getColumnIndex(DbSb.TabLoopDuration.Cols.ID))
        val deleted = getString(getColumnIndex(DbSb.TabLoopDuration.Cols.DELETED)) == "1"
        val loopDuration = LoopDuration()
        loopDuration.id = id
        loopDuration.isDeleted = deleted
        return loopDuration
    }
}