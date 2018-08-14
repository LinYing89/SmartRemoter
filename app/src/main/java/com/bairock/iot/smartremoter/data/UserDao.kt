package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.user.User

class UserDao(context: Context) {
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: UserDao? = null

        fun get(context: Context): UserDao {
            if (null == dao) {
                dao = UserDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(user: User): ContentValues {
        val values = ContentValues()
        //values.put(TabUser.Cols.EMAIL, user.getEmail());
        values.put(DbSb.TabUser.Cols.NAME, user.name)
        values.put(DbSb.TabUser.Cols.PET_NAME, user.petName)
        values.put(DbSb.TabUser.Cols.PSD, user.psd)
        //values.put(TabUser.Cols.REGISTER_TIME, user.getRegisterTime().getTime());
        //values.put(TabUser.Cols.TEL, user.getTel());
        return values
    }

    fun addUser(c: User) {
        val values = getContentValues(c)
        mDatabase.insert(DbSb.TabUser.NAME, null, values)
    }

    fun updateUser(c: User) {
        val values = getContentValues(c)
        mDatabase.update(DbSb.TabUser.NAME, values,
                "_id = ?",
                arrayOf(c.id!!.toString()))
    }

    fun getUser(): User? {
        var user: User? = null
        val cursor = queryUser()
        try {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                user = cursor.getUser()
            }
        } finally {
            cursor.close()
        }
        return user
    }

    private fun queryUser(whereClause: String, whereArgs: Array<String>): Cursor {
        return mDatabase.query(
                DbSb.TabUser.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )
    }

    private fun queryUser(): UserCursorWrapper {
        val cursor = mDatabase.query(
                DbSb.TabUser.NAME, // having
                null // orderBy
                , null, null, null, null, null
        )// Columns - null selects all columns
        // groupBy
        return UserCursorWrapper(cursor)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabUser.NAME)
    }
}