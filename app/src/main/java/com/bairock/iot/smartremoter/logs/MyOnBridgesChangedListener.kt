package com.bairock.iot.smartremoter.logs

import com.bairock.iot.intelDev.communication.DevChannelBridge
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper

class MyOnBridgesChangedListener : DevChannelBridgeHelper.OnBridgesChangedListener {

    override fun onAdd(devChannelBridge: DevChannelBridge) {
        TcpLogActivity.addRec("新连接建立:id:" + devChannelBridge.channelId)
        BridgesStateActivity.addBridge(devChannelBridge.channelId)
    }

    override fun onRemove(devChannelBridge: DevChannelBridge) {
        TcpLogActivity.addRec("连接关闭:id:" + devChannelBridge.channelId)
        BridgesStateActivity.removeBridge(devChannelBridge.channelId)
    }
}