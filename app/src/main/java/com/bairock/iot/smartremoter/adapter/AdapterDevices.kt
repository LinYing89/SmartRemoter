package com.bairock.iot.smartremoter.adapter

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer
import com.bairock.iot.intelDev.device.remoter.Curtain
import com.bairock.iot.intelDev.device.remoter.RemoterContainer
import com.bairock.iot.intelDev.device.remoter.Television
import com.bairock.iot.smartremoter.R
import java.lang.ref.WeakReference

class AdapterDevices(context : Context, private val listDevice: List<Device>) : RecyclerView.Adapter<AdapterDevices.ViewHolder>() {

    private val mInflater = LayoutInflater.from(context)
    private val listViewHolder: MutableList<AdapterDevices.ViewHolder> = arrayListOf()

    companion object {
        const val STATE = 2
        const val NAME = 3
        lateinit var handler : MyHandler
    }

    init {
        handler = MyHandler(this)
    }

    override fun getItemCount(): Int {
        return listDevice.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDevices.ViewHolder {
        val vh = AdapterDevices.ViewHolder(mInflater.inflate(R.layout.layout_devices, parent, false))
        listViewHolder.add(vh)
        return vh
    }

    override fun onBindViewHolder(holder: AdapterDevices.ViewHolder, position: Int) {
        holder.setData(listDevice[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal lateinit var device: Device
        private val textName = itemView.findViewById(R.id.txtName) as TextView
        private val txtCoding = itemView.findViewById(R.id.txtCoding) as TextView
        private val redGreen = itemView.findViewById(R.id.imgAbnormal) as ImageView
        private val imgHaveChild = itemView.findViewById(R.id.imgHaveChild) as ImageView
        private val imgDevice = itemView.findViewById(R.id.imgDevice) as ImageView

        fun setData(device: Device) {
            this.device = device
            init()
        }

        private fun init() {
            refreshName()
            refreshState()
            txtCoding.text = device.longCoding
            if(device is DevHaveChild){
                imgHaveChild.visibility = View.VISIBLE
            }else{
                imgHaveChild.visibility = View.GONE
            }

            when(device){
                is RemoterContainer ->{imgDevice.setImageResource(R.drawable.ic_remoter_container)}
                is DevCollectClimateContainer -> {imgDevice.setImageResource(R.drawable.ic_tem)}
                is Television -> {imgDevice.setImageResource(R.drawable.ic_tv)}
                is Curtain -> {imgDevice.setImageResource(R.drawable.ic_curtain)}
            }
        }

        internal fun refreshName() {
            textName.text = device.name
        }

        internal fun refreshState() {
            if (!device.isNormal) {
                redGreen.visibility = View.VISIBLE
            } else {
                redGreen.visibility = View.GONE
            }
        }
    }

    class MyHandler(activity: AdapterDevices) : Handler() {
        private var mActivity = WeakReference<AdapterDevices>(activity)

        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()!!
            val dev = msg.obj as Device
            for (vh in theActivity.listViewHolder) {
                if (vh.device === dev) {
                    when (msg.what) {
                        STATE -> vh.refreshState()
                        NAME -> vh.refreshName()
                    }
                    break
                }
            }
        }
    }
}