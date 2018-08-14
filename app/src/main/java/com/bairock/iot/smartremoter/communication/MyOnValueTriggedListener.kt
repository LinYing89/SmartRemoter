package com.bairock.iot.smartremoter.communication

import android.util.Log
import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger
import com.bairock.iot.intelDev.device.CtrlModel

class MyOnValueTriggedListener : CollectProperty.OnValueTriggedListener{

    /**
     * 事件触发
     */
    override fun onValueTrigged(p0: ValueTrigger, p1: Float) {
        Log.e("ValueTriggedListener", p0.message + " triggered value = " + p1)
        //如果服务器已连接，本地不提醒，只允许服务器推送提醒，如果服务器未连接，本地推送提醒
        if(p0.collectProperty.devCollect.ctrlModel == CtrlModel.LOCAL){
            val content = p0.collectProperty.devCollect.name + ":" + p0.message+ "(当前值:" + p1 + ")"
            pushLocal("提醒", content)
        }
    }

    /**
     * 事件解除
     */
    override fun onValueTriggedRelieve(p0: ValueTrigger, p1: Float) {
    }

    private fun pushLocal(title : String, content : String){

    }
}