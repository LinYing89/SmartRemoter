package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.user.DevGroup

class GroupCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getDevGroup(): DevGroup {
        val id = getString(getColumnIndex(DbSb.TabDevGroup.Cols.ID))
        val name = getString(getColumnIndex(DbSb.TabDevGroup.Cols.NAME))
        val petName = getString(getColumnIndex(DbSb.TabDevGroup.Cols.PET_NAME))
        val psd = getString(getColumnIndex(DbSb.TabDevGroup.Cols.PSD))
        //String userId = getString(getColumnIndex(DbSb.TabDevGroup.Cols.USER_ID));
        val group = DevGroup()
        group.id = id.toLong()
        group.name = name
        group.petName = petName
        group.psd = psd

        return group
    }
}