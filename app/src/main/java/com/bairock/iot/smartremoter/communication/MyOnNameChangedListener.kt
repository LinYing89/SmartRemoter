package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.IStateDev
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.bairock.iot.intelDev.user.MyHome
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.DeviceDao

object MyOnNameChangedListener : MyHome.OnNameChangedListener {

    override fun onNameChanged(p0: MyHome, p1: String?) {
        if(p0 is Device){
            refreshUi(device = p0)
            updateDeviceDao(device = p0)
        }
    }

    private fun refreshUi(device: Device) {
        if (device is IStateDev) {
//                if(null != ElectricalCtrlFragment.handler){
//                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.NOTIFY_ADAPTER, RecyclerAdapterElectrical3.NAME, RecyclerAdapterElectrical3.NAME, device)
//                }
        }else if(device is DevCollect){

        }
    }

    private fun updateDeviceDao(device: Device) {
        val deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT)
        deviceDao.update(device)
    }
}