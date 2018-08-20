package com.bairock.iot.smartremoter.zview

import android.content.Context
import com.bairock.iot.intelDev.device.remoter.RemoterKey

class DragRemoterKeyButton(context: Context) : RemoterKeyButton(context) {

    init {
        val rKey = RemoterKey()
        rKey.locationX = 10
        rKey.locationY = 10
        remoterKey = rKey
    }

    override fun getText(): CharSequence {
        return remoterKey.name
    }

    fun layoutBtn() {
        val localX = remoterKey.locationX
        val localY = remoterKey.locationY
        layout(localX, localY, localX + width, localY + height)
    }
}