package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.device.*

class DeviceCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getDevice(): Device {
        val id = getString(getColumnIndex(DbSb.TabDevice.Cols.ID))
        //String deviceType = getString(getColumnIndex(DbSb.TabDevice.Cols.DEVICE_TYPE));
        val alias = getString(getColumnIndex(DbSb.TabDevice.Cols.ALIAS))
        val ctrlModel = getString(getColumnIndex(DbSb.TabDevice.Cols.CTRL_MODEL))

        //设备是否可见
        var visibility = true
        val iVisibility = getColumnIndex(DbSb.TabDevice.Cols.VISIBILITY)
        if (iVisibility != -1) {
            val strVisibility = getString(iVisibility)
            if (null != strVisibility) {
                visibility = getString(getColumnIndex(DbSb.TabDevice.Cols.VISIBILITY)) == "1"
            }
        }

        val deleted = getString(getColumnIndex(DbSb.TabDevice.Cols.DELETED)) == "1"
        val devCategory = getString(getColumnIndex(DbSb.TabDevice.Cols.DEV_CATEGORY))
        val stateId = getString(getColumnIndex(DbSb.TabDevice.Cols.DEV_STATE_ID))
        val gear = getString(getColumnIndex(DbSb.TabDevice.Cols.GEAR))
        val mainCodeId = getString(getColumnIndex(DbSb.TabDevice.Cols.MAIN_CODE_ID))
        val name = getString(getColumnIndex(DbSb.TabDevice.Cols.NAME))
        val place = getString(getColumnIndex(DbSb.TabDevice.Cols.PLACE))
        val sortIndex = Integer.parseInt(getString(getColumnIndex(DbSb.TabDevice.Cols.SORT_INDEX)))
        val subCode = getString(getColumnIndex(DbSb.TabDevice.Cols.SUB_CODE))
        val panid = getString(getColumnIndex(DbSb.TabDevice.Cols.PANID))
        //String devGroupId = getString(getColumnIndex(DbSb.TabDevice.Cols.DEV_GROUP_ID));
        //String parentId = getString(getColumnIndex(DbSb.TabDevice.Cols.PARENT_ID));

        val device = DeviceAssistent.createDeviceByMcId(mainCodeId, subCode)
        device.id = id
        device.alias = alias
        device.ctrlModel = enumValueOf<CtrlModel>(ctrlModel)
        device.isVisibility = visibility
        device.isDeleted = deleted
        device.devCategory = enumValueOf<DevCategory>(devCategory)
        device.devStateId = stateId
        device.gear = enumValueOf<Gear>(gear)
        device.name = name
        device.place = place
        device.sortIndex = sortIndex
        if (device is Coordinator) {
            device.panid = panid
        }
        return device
    }
}