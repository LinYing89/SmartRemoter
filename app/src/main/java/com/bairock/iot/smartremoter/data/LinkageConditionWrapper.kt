package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.CompareSymbol
import com.bairock.iot.intelDev.linkage.LinkageCondition
import com.bairock.iot.intelDev.linkage.TriggerStyle
import com.bairock.iot.intelDev.linkage.ZLogic
import com.bairock.iot.intelDev.user.DevGroup

class LinkageConditionWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    companion object {
        var devGroup: DevGroup? = null
    }

    fun getLinkageCondition(): LinkageCondition? {
        val devId = getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.DEV_ID))
        val device = devGroup!!.findDeviceByDevId(devId) ?: return null

        val id = getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.ID))
        //String linkageId = getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.LINKAGE_ID));
        val compareSymbol = enumValueOf<CompareSymbol>(getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.COMPARE_SYMBOL)))
        val compareValue = java.lang.Float.valueOf(getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.COMPARE_VALUE)))!!
        val logic = enumValueOf<ZLogic>(getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.LOGIC)))
        val triggerStyle = enumValueOf<TriggerStyle>(getString(getColumnIndex(DbSb.TabLinkageCondition.Cols.TRIGGER_STYLE)))

        val linkageCondition = LinkageCondition()
        linkageCondition.id = id
        linkageCondition.compareSymbol = compareSymbol
        linkageCondition.compareValue = compareValue
        linkageCondition.logic = logic
        linkageCondition.triggerStyle = triggerStyle
        linkageCondition.device = device
        return linkageCondition
    }
}