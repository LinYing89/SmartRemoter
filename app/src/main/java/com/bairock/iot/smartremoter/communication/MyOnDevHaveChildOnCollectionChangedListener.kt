package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.main.CtrlFragment
import com.bairock.iot.smartremoter.main.DevicesFragment

object MyOnDevHaveChildOnCollectionChangedListener : DevHaveChild.OnDeviceCollectionChangedListener {

    override fun onAdded(device: Device) {
        refreshUi()
        HamaApp.setDeviceListener(device)
    }

    override fun onRemoved(device: Device) {
        refreshUi()
    }

    private fun refreshUi() {
        DevicesFragment.handler?.obtainMessage(DevicesFragment.RELOAD_LIST)?.sendToTarget()
        CtrlFragment.handler?.obtainMessage(DevicesFragment.RELOAD_LIST)?.sendToTarget()
    }
}