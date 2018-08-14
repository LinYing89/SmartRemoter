package com.bairock.iot.smartremoter.logs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bairock.iot.smartremoter.R

class AdapterBridgeMsg(private val context: Context, private val listDevChannelBridge: List<NetMsgType>) : BaseAdapter() {

    override fun getCount(): Int {
        return listDevChannelBridge.size
    }

    override fun getItem(position: Int): Any {
        return listDevChannelBridge[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cView = mInflater.inflate(R.layout.layout_bridge_msg, null)
        val tvTime = cView.findViewById(R.id.tvTime) as TextView
        val tvRec = cView.findViewById(R.id.tvRec) as TextView
        val tvSend = cView.findViewById(R.id.tvSend) as TextView
        val bridge = listDevChannelBridge[position]
        tvTime.text = bridge.time
        if (bridge.type == 0) {
            tvRec.text = bridge.msg
            tvSend.text = ""
        } else {
            tvRec.text = ""
            tvSend.text = bridge.msg
        }
        return cView
    }
}