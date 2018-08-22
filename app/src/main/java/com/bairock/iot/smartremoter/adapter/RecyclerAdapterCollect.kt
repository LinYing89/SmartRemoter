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
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.remoter.Curtain
import com.bairock.iot.intelDev.device.remoter.Television
import com.bairock.iot.smartremoter.R
import java.lang.ref.WeakReference

class RecyclerAdapterCollect(context: Context, private val listDevice: List<Device>) : RecyclerView.Adapter<RecyclerAdapterCollect.ViewHolder>() {

    companion object {
        const val NAME = 2
        var handler : MyHandler? = null
    }

    init {
        handler = MyHandler(this)
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var listViewHolder: MutableList<RecyclerAdapterCollect.ViewHolder> = arrayListOf()

    override fun getItemCount(): Int {
        return listDevice.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterCollect.ViewHolder {
        val vh = RecyclerAdapterCollect.ViewHolder(mInflater.inflate(R.layout.adapter_collect, parent, false))
        listViewHolder.add(vh)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerAdapterCollect.ViewHolder, position: Int) {
        holder.setData(listDevice[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal lateinit var device: Device
        private val imgDevice : ImageView = itemView.findViewById(R.id.imgDevice)
        private val textName: TextView = itemView.findViewById(R.id.txtDeviceName)

        fun setData(device: Device) {
            this.device = device
            init()
        }

        private fun init() {
            refreshName()
            refreshImg()
        }

        private fun refreshImg() {
            when(device){
                is Television ->{imgDevice.setImageResource(R.drawable.ic_tv)}
                is Curtain ->{imgDevice.setImageResource(R.drawable.ic_curtain)}
            }
        }

        internal fun refreshName() {
            textName.text = device.name
        }
    }

    class MyHandler (activity: RecyclerAdapterCollect) : Handler() {
        private var mActivity = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()
            val dev = msg.obj as Device
            for (vh in theActivity!!.listViewHolder) {
                if (vh.device == dev) {
                    when (msg.what) {
                        NAME -> vh.refreshName()
                    }
                    break
                }
            }
        }
    }
}