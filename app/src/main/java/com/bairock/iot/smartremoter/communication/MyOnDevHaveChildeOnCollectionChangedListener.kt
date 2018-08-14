package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.smartremoter.app.HamaApp

object MyOnDevHaveChildeOnCollectionChangedListener : DevHaveChild.OnDeviceCollectionChangedListener {

    override fun onAdded(device: Device) {
        refreshUi()
        HamaApp.setDeviceListener(device)
    }

    override fun onRemoved(device: Device) {
        refreshUi()
    }

    private fun refreshUi() {
//        if (null != SearchActivity.handler) {
//            SearchActivity.handler.obtainMessage(SearchActivity.handler.UPDATE_LIST).sendToTarget()
//        }
//        if (null != ElectricalCtrlFragment.handler) {
//            ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget()
//        }
    }
}