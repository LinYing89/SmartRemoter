package com.bairock.iot.smartremoter.logs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bairock.iot.smartremoter.R

class AdapterBridgeState(private val context: Context, private val listDevChannelBridge: List<BridgeState>) : BaseAdapter() {

    override fun getCount(): Int {
        return listDevChannelBridge.size
    }

    override fun getItem(position: Int): Any {
        return listDevChannelBridge[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cView = mInflater.inflate(R.layout.layout_bridge_state, null)
        val tvDevice = cView.findViewById(R.id.tvDevice) as TextView
        val tvChannelId = cView.findViewById(R.id.tvChannelId) as TextView
        val tvSendCount = cView.findViewById(R.id.tvSendCount) as TextView
        val tvReceivedCount = cView.findViewById(R.id.tvReceivedCount) as TextView
        val bridge = listDevChannelBridge[position]
        if (null != bridge.getDevCoding()) {
            tvDevice.text = bridge.getDevCoding()
        } else {
            tvDevice.text = "未知"
        }
        tvChannelId.text = bridge.channelId
        tvSendCount.text = bridge.sendCount.toString()
        tvReceivedCount.text = bridge.recCount.toString()
        return convertView
    }
}