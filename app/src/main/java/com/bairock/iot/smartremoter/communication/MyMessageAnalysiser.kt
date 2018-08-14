package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.communication.MessageAnalysiser
import com.bairock.iot.intelDev.device.*
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.DeviceDao
import com.bairock.iot.smartremoter.esptouch.EspTouchAddDevice

class MyMessageAnalysiser : MessageAnalysiser() {

    override fun receivedMsg(msg: String?) {
        //TcpLogActivity.addRec(msg);
    }

    override fun deviceFeedback(device: Device, msg: String) {
        updateDevice(device)
    }

    private fun updateDevice(device: Device) {
        if (device.ctrlModel != CtrlModel.LOCAL) {
            device.ctrlModel = CtrlModel.LOCAL
        }
    }

    override fun unKnowDev(device: Device, s: String) {
        //新设备，未在本系统中
        //addNewDevice(device);
    }

    override fun unKnowMsg(msg: String) {

    }

    override fun allMessageEnd() {

    }

    override fun singleMessageStart(msg: String): Boolean {
        var message = msg
        if (message.startsWith("!")) {
            if (message.contains("#")) {
                message = message.substring(0, message.indexOf("#"))
            }
            val codings = message.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (codings.size < 2) {
                return false
            }
            //Device device = DeviceAssistent.createDeviceByCoding(codings[1]);
            val device = HamaApp.DEV_GROUP.findDeviceWithCoding(codings[1])
            if (null == device || device !is Coordinator) {
                return false
            }

            val coordinator = device
            if (!coordinator.isConfigingChildDevice) {
                return false
            }
            for (i in 2 until codings.size) {
                val coding = codings[i]
                var device1: Device? = coordinator.findDevByCoding(coding)
                if (null == device1) {
                    device1 = DeviceAssistent.createDeviceByCoding(coding)
                    if (device1 != null) {
                        HamaApp.DEV_GROUP.createDefaultDeviceName(device1)
                        coordinator.addChildDev(device1)
                        val deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT)
                        deviceDao.add(device1)
                    }
                }
            }
//            if (null != SearchActivity.handler) {
//                SearchActivity.handler.obtainMessage(SearchActivity.handler.DEV_ADD_CHILD).sendToTarget()
//            }
            return false
        }
        return true
    }

    override fun singleMessageEnd(device: Device, msg: String) {

    }

    override fun configDevice(device: Device, s: String) {
        addNewDevice(device)
    }

    override fun configDeviceCtrlModel(device: Device, s: String) {
    }

    private fun addNewDevice(device: Device) {
        if (EspTouchAddDevice.CONFIGING) {
            HamaApp.DEV_GROUP.createDefaultDeviceName(device)
            EspTouchAddDevice.DEVICE = device
        }
    }
}