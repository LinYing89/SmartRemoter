package com.bairock.iot.smartremoter.logs

import java.text.SimpleDateFormat
import java.util.*

class BridgeState {
    private var devCoding: String? = null
    var channelId: String? = null
    var recCount: Int = 0
    var sendCount: Int = 0
    var listBridgeMsgType: MutableList<NetMsgType> = arrayListOf()

    var onCollectionChangedMsgListener: OnCollectionChangedMsgListener? = null
    var onDevCodingChangedListener: OnDevCodingChangedListener? = null

    fun setDevCoding(devCoding: String?) {
        if (devCoding == null) {
            return
        }
        if (this.devCoding == null || this.devCoding != devCoding) {
            this.devCoding = devCoding
            if (null != onDevCodingChangedListener) {
                onDevCodingChangedListener!!.onDevCodingChanged()
            }
        }
    }

    fun  getDevCoding() : String?{
        return devCoding
    }

    fun addMsg(type: Int, msg: String) {
        if (listBridgeMsgType.size > 50) {
            val nmt = listBridgeMsgType.removeAt(0)
            if (null != onCollectionChangedMsgListener) {
                onCollectionChangedMsgListener!!.onRemovedMsg(nmt)
            }
        }
        val netMsgType = NetMsgType()
        netMsgType.type = type
        netMsgType.msg = msg
        val dft = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
        netMsgType.time = dft.format(Date())
        listBridgeMsgType.add(netMsgType)
        if (null != onCollectionChangedMsgListener) {
            onCollectionChangedMsgListener!!.onAddMsg(netMsgType)
        }
    }

    interface OnCollectionChangedMsgListener {
        fun onAddMsg(netMsgType: NetMsgType)
        fun onRemovedMsg(netMsgType: NetMsgType)
    }

    interface OnDevCodingChangedListener {
        fun onDevCodingChanged()
    }
}