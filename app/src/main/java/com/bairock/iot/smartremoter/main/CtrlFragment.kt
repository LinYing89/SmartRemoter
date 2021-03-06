package com.bairock.iot.smartremoter.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.remoter.Curtain
import com.bairock.iot.intelDev.device.remoter.CustomRemoter
import com.bairock.iot.intelDev.device.remoter.Television
import com.bairock.iot.intelDev.user.DevGroup
import com.bairock.iot.intelDev.user.IntelDevHelper

import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.adapter.RecyclerAdapterCollect
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.remoter.CurtainActivity
import com.bairock.iot.smartremoter.remoter.DragRemoterActivity
import com.bairock.iot.smartremoter.remoter.TelevisionActivity
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener
import kotlinx.android.synthetic.main.fragment_ctrl.*
import java.lang.ref.WeakReference

class CtrlFragment : BaseFragment() {

    private lateinit var listShowDevices: List<Device>
    private var adapterDevices: RecyclerAdapterCollect? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ctrl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeMenuRecyclerViewDevice.layoutManager = GridLayoutManager(this.context, 3)
        swipeMenuRecyclerViewDevice.setSwipeItemClickListener(onItemClickListener)
        setDeviceList()
        handler = MyHandler(this)

        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener)

        val devClimate = HamaApp.findClimate(HamaApp.DEV_GROUP.listDevice)

        if(devClimate != null){
            devClimate.findTemperatureDev().collectProperty.currentValue = 19.26f
            devClimate.findHumidityDev().collectProperty.currentValue = 62f
            txtTem.text = devClimate.findTemperatureDev().collectProperty.valueWithSymbol
            txtHum.text = devClimate.findHumidityDev().collectProperty.valueWithSymbol
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RecyclerAdapterCollect.handler = null
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener)
    }

    private val onDeviceCollectionChangedListener = object : DevGroup.OnDeviceCollectionChangedListener {
        override fun onAdded(device: Device) {
            handler!!.obtainMessage(DevicesFragment.RELOAD_LIST).sendToTarget()
        }

        override fun onRemoved(device: Device) {
            handler!!.obtainMessage(DevicesFragment.RELOAD_LIST).sendToTarget()
        }
    }

    private fun setDeviceList() {
        listShowDevices = HamaApp.DEV_GROUP.findListIStateDev(true)
        adapterDevices = RecyclerAdapterCollect(this.context!!, listShowDevices)
        swipeMenuRecyclerViewDevice.adapter = adapterDevices
    }

    private val onItemClickListener = SwipeItemClickListener { _: View, i: Int ->
        IntelDevHelper.OPERATE_DEVICE = listShowDevices[i]
        when(IntelDevHelper.OPERATE_DEVICE){
            is Television -> {
                val intent = Intent(this.context, TelevisionActivity::class.java)
                intent.putExtra("coding", IntelDevHelper.OPERATE_DEVICE.longCoding)
                startActivity(intent)
            }
            is Curtain ->{
                val intent = Intent(this.context, CurtainActivity::class.java)
                intent.putExtra("coding", IntelDevHelper.OPERATE_DEVICE.longCoding)
                startActivity(intent)
            }
            is CustomRemoter ->{
                val intent = Intent(this.context, DragRemoterActivity::class.java)
                intent.putExtra("coding", IntelDevHelper.OPERATE_DEVICE.longCoding)
                startActivity(intent)
            }
        }
    }

    class MyHandler(fragment: CtrlFragment) : Handler() {
        private var mActivity = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val mFragment = mActivity.get()!!
            when (msg.what) {
                DevicesFragment.RELOAD_LIST -> {
                    mFragment.setDeviceList()
                }
                REFRESH_TEM -> {
                    mFragment.txtTem.text = msg.obj.toString()
                }
                REFRESH_HUM -> {
                    mFragment.txtHum.text = msg.obj.toString()
                }
            }
        }
    }

    companion object {
        var handler: MyHandler? = null

        var REFRESH_TEM = 1
        var REFRESH_HUM = 2

        @JvmStatic
        fun newInstance() =
                CtrlFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
