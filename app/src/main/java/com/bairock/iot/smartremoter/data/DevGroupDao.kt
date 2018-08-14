package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.user.DevGroup

class DevGroupDao(context: Context) {

    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var devGroupDao: DevGroupDao? = null

        fun get(context: Context): DevGroupDao {
            if (null == devGroupDao) {
                devGroupDao = DevGroupDao(context)
            }
            return devGroupDao!!
        }
    }

    private fun getContentValues(group: DevGroup): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabDevGroup.Cols.ID, group.id)
        values.put(DbSb.TabDevGroup.Cols.NAME, group.name)
        values.put(DbSb.TabDevGroup.Cols.PET_NAME, group.petName)
        values.put(DbSb.TabDevGroup.Cols.PSD, group.psd)
        values.put(DbSb.TabDevGroup.Cols.USER_ID, group.user.id)
        return values
    }

    fun add(group: DevGroup) {
        val values = getContentValues(group)
        mDatabase.insert(DbSb.TabDevGroup.NAME, null, values)
    }

    fun update(group: DevGroup) {
        val values = getContentValues(group)
        mDatabase.update(DbSb.TabDevGroup.NAME, values,
                "id = ?",
                arrayOf<String>(group.id.toString()))
    }

    fun find(): DevGroup? {
        var user: DevGroup? = null
        val cursor = query()
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst()
                user = cursor.getDevGroup()
            }
        } finally {
            cursor.close()
        }
        return user
    }

    private fun query(whereClause: String, whereArgs: Array<String>): Cursor {
        return mDatabase.query(
                DbSb.TabDevGroup.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )
    }

    private fun query(): GroupCursorWrapper {
        val cursor = mDatabase.query(
                DbSb.TabDevGroup.NAME, // having
                null // orderBy
                , null, null, null, null, null
        )// Columns - null selects all columns
        // groupBy
        return GroupCursorWrapper(cursor)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabDevGroup.NAME)
    }
}