package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.user.User
import java.util.*

class UserCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getUser(): User {
        val id = getLong(getColumnIndex(DbSb.TabUser.Cols.ID))
        val email = getString(getColumnIndex(DbSb.TabUser.Cols.EMAIL))
        val name = getString(getColumnIndex(DbSb.TabUser.Cols.NAME))
        val petName = getString(getColumnIndex(DbSb.TabUser.Cols.PET_NAME))
        val psd = getString(getColumnIndex(DbSb.TabUser.Cols.PSD))
        val date = getLong(getColumnIndex(DbSb.TabUser.Cols.REGISTER_TIME))
        val tel = getString(getColumnIndex(DbSb.TabUser.Cols.TEL))
        val user = User()
        user.id = id
        user.email = email
        user.name = name
        user.petName = petName
        user.psd = psd
        user.registerTime = Date(date)
        user.tel = tel
        return user
    }
}