package com.bairock.iot.smartremoter.logs

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper
import com.bairock.iot.smartremoter.R
import kotlinx.android.synthetic.main.activity_bridges_state.*
import java.lang.ref.WeakReference
import java.util.ArrayList

class BridgesStateActivity : AppCompatActivity() {

    companion object {
        var myHandler: MyHandler? = null
        val listBridgeState = ArrayList<BridgeState>()

        fun sendCountAnd(channelId: String, count: Int, msg: String) {
            for (bridgeState in listBridgeState) {
                if (bridgeState.channelId.equals(channelId)) {
                    bridgeState.sendCount = count
                    bridgeState.addMsg(1, msg)
                    if (null != myHandler) {
                        myHandler!!.obtainMessage(0).sendToTarget()
                    }
                    break
                }
            }
        }

        fun recCountAnd(devCoding: String, channelId: String, count: Int, msg: String) {
            for (bridgeState in listBridgeState) {
                if (bridgeState.channelId.equals(channelId)) {
                    bridgeState.recCount = count
                    bridgeState.addMsg(0, msg)
                    //                if(bridgeState.getDevCoding() == null){
                    bridgeState.setDevCoding(devCoding)
                    //                }
                    if (null != myHandler) {
                        myHandler!!.obtainMessage(0).sendToTarget()
                    }
                    break
                }
            }
        }

        fun addBridge(channelId: String) {
            val bridgeState = BridgeState()
            bridgeState.channelId = channelId
            listBridgeState.add(bridgeState)
            if (null != myHandler) {
                myHandler!!.obtainMessage(0).sendToTarget()
            }
        }

        fun removeBridge(channelId: String) {
            for (bridgeState in listBridgeState) {
                if (bridgeState.channelId.equals(channelId)) {
                    listBridgeState.remove(bridgeState)
                    break
                }
            }
            if (null != myHandler) {
                myHandler!!.obtainMessage(0).sendToTarget()
            }
        }
    }

    private var adapterBridgeState: AdapterBridgeState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridges_state)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val list = ArrayList(listBridgeState)
        for (bridgeState in list) {
            var had = false
            for (db in DevChannelBridgeHelper.getIns().listDevChannelBridge) {
                if (db.channelId == bridgeState.channelId) {
                    had = true
                    break
                }
            }
            if (!had) {
                listBridgeState.remove(bridgeState)
            }
        }
        setAdapter()
        setListener()
        myHandler = MyHandler(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bridge_state, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish() // back button
                return true
            }
            R.id.menu_udp -> startActivity(Intent(this@BridgesStateActivity, UdpLogActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        myHandler = null
    }

    private fun setAdapter() {
        adapterBridgeState = AdapterBridgeState(this, listBridgeState)
        lvBridge.adapter = adapterBridgeState
    }

    private fun setListener() {
        lvBridge.setOnItemClickListener { parent, view, position, id ->
            BridgeMsgTestActivity.bridgeState = listBridgeState[position]
            startActivity(Intent(this@BridgesStateActivity, BridgeMsgTestActivity::class.java))
        }
    }

    class MyHandler(activity : BridgesStateActivity) : Handler(){
        private var mActivity = WeakReference<BridgesStateActivity>(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val theActivity = mActivity.get()
            when (msg.what) {
                0 ->
                    theActivity!!.adapterBridgeState!!.notifyDataSetChanged()
            }
        }
    }
}
