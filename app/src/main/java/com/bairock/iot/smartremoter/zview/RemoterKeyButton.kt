package com.bairock.iot.smartremoter.zview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.Constant

open class RemoterKeyButton : android.support.v7.widget.AppCompatButton {

    var remoterKey: RemoterKey = RemoterKey()
    set(value) {
        field = value
        text = value.name
    }

    constructor(context: Context?) : super(context){
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        val width = Constant.getRemoterKeyWidth()
        setWidth(width)
        height = width
        setTextColor(Color.BLACK)
        setBackgroundResource(R.drawable.sharp_custom_btn)
        text = remoterKey.name
    }
}