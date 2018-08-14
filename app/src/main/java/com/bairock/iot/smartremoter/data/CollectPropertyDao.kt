package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.DevCollect

class CollectPropertyDao(mContext: Context) {

    private val context = mContext;

    private val mDatabase: SQLiteDatabase = SdDbHelper(mContext).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var collectPropertyDao: CollectPropertyDao? = null

        fun get(context: Context): CollectPropertyDao {
            if (null == collectPropertyDao) {
                collectPropertyDao = CollectPropertyDao(context)
            }
            return collectPropertyDao!!
        }
    }

    private fun getContentValues(collectProperty: CollectProperty): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabCollectProperty.Cols.ID, collectProperty.id)
        values.put(DbSb.TabCollectProperty.Cols.CREST_VALUE, collectProperty.crestValue)
        values.put(DbSb.TabCollectProperty.Cols.CREST_REFER_VALUE, collectProperty.crestReferValue)
        values.put(DbSb.TabCollectProperty.Cols.CURRENT_VALUE, collectProperty.currentValue)
        values.put(DbSb.TabCollectProperty.Cols.LEAST_VALUE, collectProperty.leastValue)
        values.put(DbSb.TabCollectProperty.Cols.LEAST_REFER_VALUE, collectProperty.leastReferValue)
        values.put(DbSb.TabCollectProperty.Cols.PERCENT, collectProperty.percent)
        values.put(DbSb.TabCollectProperty.Cols.CALIBRATION_VALUE, collectProperty.calibrationValue)
        values.put(DbSb.TabCollectProperty.Cols.FORMULA, collectProperty.formula)
        if (null != collectProperty.collectSrc) {
            values.put(DbSb.TabCollectProperty.Cols.SIGNAL_SRC, collectProperty.collectSrc.toString())
        }
        if (null != collectProperty.unitSymbol) {
            values.put(DbSb.TabCollectProperty.Cols.UNIT_SYMBOL, collectProperty.unitSymbol)
        }
        values.put(DbSb.TabCollectProperty.Cols.DEV_COLLECT_ID, collectProperty.devCollect.id)
        return values
    }

    fun add(collectProperty: CollectProperty) {
        val collectProperty1 = find(collectProperty.devCollect)
        if (null != collectProperty1) {
            return
        }
        val values = getContentValues(collectProperty)
        mDatabase.insert(DbSb.TabCollectProperty.NAME, null, values)

        val valueTriggerDao = ValueTriggerDao.Companion.get(context)
        for (trigger in collectProperty.listValueTrigger) {
            valueTriggerDao.add(trigger)
        }
    }

    fun delete(collectProperty: CollectProperty) {
        mDatabase.delete(DbSb.TabCollectProperty.NAME, DbSb.TabCollectProperty.Cols.ID + "=?", arrayOf(collectProperty.id))
        val valueTriggerDao = ValueTriggerDao.Companion.get(context)
        for (trigger in collectProperty.listValueTrigger) {
            valueTriggerDao.delete(trigger)
        }
    }

    fun find(devCollect: DevCollect): CollectProperty? {
        return find(DbSb.TabCollectProperty.Cols.DEV_COLLECT_ID + " = ?", arrayOf(devCollect.id))
    }

    fun find(whereClause: String, whereArgs: Array<String>): CollectProperty? {
        var collectProperty: CollectProperty? = null
        val cursor = query(whereClause, whereArgs)
        try {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                collectProperty = cursor.getCollectProperty()
            }
        } finally {
            cursor.close()
        }
        if (null != collectProperty) {
            val list = ValueTriggerDao.Companion.get(context).find(collectProperty)
            collectProperty.listValueTrigger = list
        }
        return collectProperty
    }

    fun update(collectProperty: CollectProperty) {
        val values = getContentValues(collectProperty)
        mDatabase.update(DbSb.TabCollectProperty.NAME, values,
                "id = ?",
                arrayOf(collectProperty.id))
    }

    private fun query(whereClause: String, whereArgs: Array<String>): CollectPropertyCursorWrapper {
        val cursor = mDatabase.query(
                DbSb.TabCollectProperty.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return CollectPropertyCursorWrapper(cursor)
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabCollectProperty.NAME)
    }
}