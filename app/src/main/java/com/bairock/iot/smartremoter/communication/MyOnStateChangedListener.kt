package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.communication.RefreshCollectorValueHelper
import com.bairock.iot.intelDev.device.CtrlModel
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer
import com.bairock.iot.intelDev.device.devswitch.SubDev
import com.bairock.iot.smartremoter.adapter.AdapterDevices
import com.bairock.iot.smartremoter.app.HamaApp

class MyOnStateChangedListener : Device.OnStateChangedListener {
    override fun onNormalToAbnormal(p0: Device?) {
        //refreshUi(p0!!)
        HamaApp.addOfflineDevCoding(p0)
        //本地设备才往服务器发送状态，远程设备只接收服务器状态
        if (p0 !is SubDev && p0!!.findSuperParent().ctrlModel == CtrlModel.LOCAL) {
            //PadClient.getIns().send(device.createAbnormalOrder())
        }
        if (p0 is DevCollectClimateContainer) {
            RefreshCollectorValueHelper.getIns().endRefresh(p0)
        }
    }

    override fun onNoResponse(p0: Device?) {
        //refreshUi(p0!!)
    }

    override fun onStateChanged(p0: Device?, p1: String?) {
        refreshUi(p0!!)
    }

    override fun onAbnormalToNormal(p0: Device?) {
        HamaApp.removeOfflineDevCoding(p0)

        if (p0 is DevCollectClimateContainer) {
            RefreshCollectorValueHelper.getIns().RefreshDev(p0)
        }
    }

    private fun refreshUi(device: Device) {
        if (device.parent == null) {
            AdapterDevices.handler?.obtainMessage(AdapterDevices.STATE, device)?.sendToTarget()
        }
    }
}