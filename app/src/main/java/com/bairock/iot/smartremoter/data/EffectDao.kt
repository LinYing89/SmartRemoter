package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.linkage.Effect
import java.util.ArrayList

class EffectDao(context: Context) {

    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: EffectDao? = null

        fun get(context: Context): EffectDao {
            if (null == dao) {
                dao = EffectDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(effect: Effect, linkageId: String?): ContentValues {
        val values = ContentValues()
        if (null != linkageId) {
            values.put(DbSb.TabEffect.Cols.LINKAGE_ID, linkageId)
        }
        values.put(DbSb.TabEffect.Cols.ID, effect.id)
        values.put(DbSb.TabEffect.Cols.DS_ID, effect.dsId)
        values.put(DbSb.TabEffect.Cols.DEV_ID, effect.device.id)
        values.put(DbSb.TabEffect.Cols.DELETED, effect.isDeleted)
        values.put(DbSb.TabEffect.Cols.EFFECT_CONTENT, effect.effectContent)
        values.put(DbSb.TabEffect.Cols.EFFECT_COUNT, effect.effectCount)
        return values
    }

    fun add(effect: Effect, linkageId: String) {
        val list = find(DbSb.TabEffect.Cols.ID + " = ?", arrayOf(effect.id))
        if (list.size > 0) {
            update(effect, linkageId)
        } else {
            val values1 = getContentValues(effect, linkageId)
            mDatabase.insert(DbSb.TabEffect.NAME, null, values1)
        }
    }

    fun delete(effect: Effect) {
        deleteById(effect.id)
    }

    fun deleteById(id: String) {
        mDatabase.delete(DbSb.TabEffect.NAME, DbSb.TabEffect.Cols.ID + "=?", arrayOf(id))
    }

    fun update(effect: Effect, linkageId: String) {
        val values = getContentValues(effect, linkageId)
        mDatabase.update(DbSb.TabEffect.NAME, values,
                "id = ?",
                arrayOf(effect.id))
    }

    private fun find(whereClause: String, whereArgs: Array<String>): List<Effect> {
        val cursor = query(whereClause, whereArgs)
        return createEffect(cursor)
    }

    fun findByLinkageId(linkageId: String): List<Effect> {
        val whereClause = DbSb.TabEffect.Cols.LINKAGE_ID + " = ?"
        val whereArgs = arrayOf(linkageId)
        val cursor = query(whereClause, whereArgs)
        return createEffect(cursor)
    }

    private fun query(whereClause: String, whereArgs: Array<String>): EffectWrapper {
        val cursor = mDatabase.query(
                DbSb.TabEffect.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return EffectWrapper(cursor)
    }

    private fun createEffect(cursor: EffectWrapper): List<Effect> {
        val listEffect = ArrayList<Effect>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val effect = cursor.getEffect()
                if (null != effect) {
                    listEffect.add(effect)
                }
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listEffect
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabEffect.NAME)
    }
}