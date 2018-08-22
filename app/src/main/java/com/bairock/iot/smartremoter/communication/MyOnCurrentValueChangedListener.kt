package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.bairock.iot.intelDev.device.devcollect.Humidity
import com.bairock.iot.intelDev.device.devcollect.Temperature
import com.bairock.iot.smartremoter.main.CtrlFragment

object MyOnCurrentValueChangedListener : CollectProperty.OnCurrentValueChangedListener {
    override fun onCurrentValueChanged(p0: DevCollect?, p1: Float?) {
        if (null != CtrlFragment.handler) {
            if(p0 is Temperature){
                CtrlFragment.handler!!.obtainMessage(CtrlFragment.REFRESH_TEM, p1).sendToTarget()
            }else if(p0 is Humidity){
                CtrlFragment.handler!!.obtainMessage(CtrlFragment.REFRESH_HUM, p1).sendToTarget()
            }
        }
    }
}