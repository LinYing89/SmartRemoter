package com.bairock.iot.smartremoter.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.support.multidex.MultiDex
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper
import com.bairock.iot.intelDev.communication.DevServer
import com.bairock.iot.intelDev.communication.FindDevHelper
import com.bairock.iot.intelDev.device.Coordinator
import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.bairock.iot.intelDev.device.remoter.RemoterContainer
import com.bairock.iot.intelDev.user.DevGroup
import com.bairock.iot.intelDev.user.User
import com.bairock.iot.smartremoter.communication.*

class HamaApp : Application() {

    companion object {
        var USER: User = User()
        var DEV_GROUP: DevGroup = DevGroup()
        var DEV_SERVER: DevServer? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var HAMA_CONTEXT : Context

        var NET_CONNECTED: Boolean = false

        fun setDeviceListener(device: Device) {
            device.setOnSortIndexChangedListener(MyOnSortIndexChangedListener())
            device.addOnNameChangedListener(MyOnNameChangedListener)
            device.onStateChanged = MyOnStateChangedListener()

            if (device is DevHaveChild) {
                //协调器添加子设备集合改变监听器
                if (device is Coordinator) {
                    device.addOnDeviceCollectionChangedListener(MyOnDevHaveChildeOnCollectionChangedListener)
                } else if (device is RemoterContainer) {
                    device.addOnDeviceCollectionChangedListener(MyOnDevHaveChildeOnCollectionChangedListener)
                    device.onRemoterOrderSuccessListener = MyOnRemoterOrderSuccessListener()
                }
                for (device1 in device.listDev) {
                    setDeviceListener(device1)
                }
            }
            if (device is DevCollect) {
                val cp = device.collectProperty
                cp.addOnCurrentValueChangedListener(MyOnCurrentValueChangedListener)
                cp.setOnUnitSymbolChangedListener(MyOnUnitSymbolChangedListener())
                cp.setOnValueTriggedListener(MyOnValueTriggedListener())
            }
        }

        fun addOfflineDevCoding(device: Device?) {
            if (null != device) {
                if (device is Coordinator) {
                    FindDevHelper.getIns().findDev(device.coding)
                } else if (device.findSuperParent() !is Coordinator) {
                    FindDevHelper.getIns().findDev(device.findSuperParent().coding)
                }
            }
        }

        fun removeOfflineDevCoding(device: Device?) {
            if (null != device) {
                if (null == device.parent || device.findSuperParent() !is Coordinator) {
                    FindDevHelper.getIns().alreadyFind(device.findSuperParent().coding)
                }
            }
        }

        fun sendOrder(device: Device, order: String, immediately: Boolean) {
            DevChannelBridgeHelper.getIns().sendDevOrder(device, order, immediately)
        }
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        HAMA_CONTEXT = this.applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        USER.addGroup(DEV_GROUP)

        LogUtils.init(this)
    }

}