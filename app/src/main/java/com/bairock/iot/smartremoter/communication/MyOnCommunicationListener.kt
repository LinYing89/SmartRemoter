package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.communication.DevChannelBridge
import com.bairock.iot.smartremoter.logs.BridgesStateActivity
import com.bairock.iot.smartremoter.logs.TcpLogActivity

class MyOnCommunicationListener : DevChannelBridge.OnCommunicationListener {
    override fun onSend(devChannelBridge: DevChannelBridge, s: String) {
        TcpLogActivity.addSend("id:" + devChannelBridge.channelId + " - " + s)
        devChannelBridge.sendCountAnd1()
        devChannelBridge.lastSendMsg = s
        BridgesStateActivity.sendCountAnd(devChannelBridge.channelId, devChannelBridge.sendCount, s)
    }

    override fun onReceived(devChannelBridge: DevChannelBridge, s: String) {
        TcpLogActivity.addRec("id:" + devChannelBridge.channelId + " - " + s)
        devChannelBridge.receivedCountAnd1()
        devChannelBridge.lastReceivedMsg = s
        var devCoding = ""
        val device = devChannelBridge.device
        if (null != device) {
            devCoding = device.coding
        }
        BridgesStateActivity.recCountAnd(devCoding, devChannelBridge.channelId, devChannelBridge.receivedCount, s)
    }
}