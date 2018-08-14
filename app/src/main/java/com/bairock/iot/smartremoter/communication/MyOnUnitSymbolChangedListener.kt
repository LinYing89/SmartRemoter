package com.bairock.iot.smartremoter.communication

import com.bairock.iot.intelDev.device.devcollect.CollectProperty
import com.bairock.iot.intelDev.device.devcollect.DevCollect

class MyOnUnitSymbolChangedListener : CollectProperty.OnUnitSymbolChangedListener{

    override fun onUnitSymbolChanged(p0: DevCollect?, p1: String?) {
//        if (null != ClimateFragment.handler) {
//            ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.SYMBOL, RecyclerAdapterCollect.SYMBOL, p0).sendToTarget()
//        }
    }
}