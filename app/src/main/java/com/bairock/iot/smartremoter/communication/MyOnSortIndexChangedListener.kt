package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.DeviceDao

class MyOnSortIndexChangedListener : Device.OnSortIndexChangedListener {
    override fun onSortIndexChanged(device: Device, i: Int) {
        DeviceDao.get(HamaApp.HAMA_CONTEXT).update(device)
    }
}