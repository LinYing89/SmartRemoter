package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.remoter.RemoterContainer
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.remoter.StudyKeyActivity

class MyOnRemoterOrderSuccessListener : RemoterContainer.OnRemoterOrderSuccessListener {

    override fun onRemoterOrderSuccess(p0: RemoterKey?) {
        if(null != StudyKeyActivity.remoterKey){
            if(null != StudyKeyActivity.handler) {
                StudyKeyActivity.handler!!.obtainMessage().sendToTarget()
            }
        }
    }
}