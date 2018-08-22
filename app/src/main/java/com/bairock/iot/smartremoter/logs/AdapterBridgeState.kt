package com.bairock.iot.smartremoter.logs

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bairock.iot.smartremoter.R

class AdapterBridgeState(context: Context, private val listDevChannelBridge: List<BridgeState>) : RecyclerView.Adapter<AdapterBridgeState.ViewHolder>() {

    private val mInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return listDevChannelBridge.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterBridgeState.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.layout_bridge_state, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterBridgeState.ViewHolder, position: Int) {
        holder.setData(listDevChannelBridge[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var bridge: BridgeState
        private val txtDev = itemView.findViewById(R.id.tvDevice) as TextView
        private val txtChannelId = itemView.findViewById(R.id.tvChannelId) as TextView
        private val txtSendCount = itemView.findViewById(R.id.tvSendCount) as TextView
        private val txtReceivedCount = itemView.findViewById(R.id.tvReceivedCount) as TextView

        fun setData(bridge: BridgeState) {
            this.bridge = bridge
            init()
        }

        private fun init() {
            if (null != bridge.getDevCoding()) {
                txtDev.text = bridge.getDevCoding()
            } else {
                txtDev.text = "未知"
            }
            txtChannelId.text = bridge.channelId
            txtSendCount.text = bridge.sendCount.toString()
            txtReceivedCount.text = bridge.recCount.toString()
        }
    }
}