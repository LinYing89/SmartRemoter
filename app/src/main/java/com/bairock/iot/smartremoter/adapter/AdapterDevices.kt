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
import com.bairock.iot.intelDev.device.Coordinator
import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer
import com.bairock.iot.intelDev.device.devcollect.Formaldehyde
import com.bairock.iot.intelDev.device.devcollect.Humidity
import com.bairock.iot.intelDev.device.devcollect.Temperature
import com.bairock.iot.intelDev.device.remoter.*
import com.bairock.iot.smartremoter.R
import java.lang.ref.WeakReference

class AdapterDevices(context : Context, private val listDevice: List<Device>) : RecyclerView.Adapter<AdapterDevices.ViewHolder>() {

    private val mInflater = LayoutInflater.from(context)
    private val listViewHolder: MutableList<AdapterDevices.ViewHolder> = arrayListOf()

    companion object {
        const val STATE = 2
        const val NAME = 3
        var handler : MyHandler? = null
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
            if(device is DevHaveChild || device is CustomRemoter){
                imgHaveChild.visibility = View.VISIBLE
            }else{
                imgHaveChild.visibility = View.GONE
            }

            when(device){
                is RemoterContainer ->{imgDevice.setImageResource(R.drawable.ic_remoter_container)}
                is DevCollectClimateContainer, is Temperature -> {imgDevice.setImageResource(R.drawable.ic_tem)}
                is Humidity -> {imgDevice.setImageResource(R.drawable.ic_hum)}
                is Formaldehyde -> {imgDevice.setImageResource(R.drawable.ic_hcho)}
                is Coordinator -> {imgDevice.setImageResource(R.drawable.ic_device)}
                is Television -> {imgDevice.setImageResource(R.drawable.ic_tv)}
                is Curtain -> {imgDevice.setImageResource(R.drawable.ic_curtain)}
                is Remoter -> {imgDevice.setImageResource(R.drawable.ic_custom_remoter)}
            }
        }

        internal fun refreshName() {
            textName.text = device.name
        }

        internal fun refreshState() {
            if(device.parent != null){
                redGreen.visibility = View.GONE
                return
            }
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