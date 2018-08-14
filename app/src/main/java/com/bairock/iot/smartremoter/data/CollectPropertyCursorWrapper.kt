package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource

class CollectPropertyCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getCollectProperty(): CollectProperty {
        val id = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.ID))
        val crestValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CREST_VALUE))
        val crestReferValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CREST_REFER_VALUE))
        val currentValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CURRENT_VALUE))
        val leastValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.LEAST_VALUE))
        val leastReferValue = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.LEAST_REFER_VALUE))
        val percent = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.PERCENT))
        val formula = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.FORMULA))
        val calibration = getFloat(getColumnIndex(DbSb.TabCollectProperty.Cols.CALIBRATION_VALUE))

        var collectSignalSource = CollectSignalSource.DIGIT
        val iSignalSrc = getColumnIndex(DbSb.TabCollectProperty.Cols.SIGNAL_SRC)
        if (iSignalSrc != -1) {
            val signalSrc = getString(iSignalSrc)
            if (null != signalSrc) {
                try {
                    collectSignalSource = enumValueOf(signalSrc)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        val unitSymbol = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.UNIT_SYMBOL))
        //String devCollectId = getString(getColumnIndex(DbSb.TabCollectProperty.Cols.DEV_COLLECT_ID));

        val collectProperty = CollectProperty()
        collectProperty.id = id
        collectProperty.crestValue = crestValue
        collectProperty.crestReferValue = crestReferValue
        collectProperty.currentValue = currentValue
        collectProperty.leastValue = leastValue
        collectProperty.leastReferValue = leastReferValue
        collectProperty.percent = percent
        collectProperty.collectSrc = collectSignalSource
        collectProperty.unitSymbol = unitSymbol
        collectProperty.formula = formula
        collectProperty.calibrationValue = calibration
        return collectProperty
    }
}