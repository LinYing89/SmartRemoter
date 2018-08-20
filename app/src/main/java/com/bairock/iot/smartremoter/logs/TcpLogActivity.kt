package com.bairock.iot.smartremoter.logs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import com.bairock.iot.smartremoter.R
import kotlinx.android.synthetic.main.activity_tcp_log.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class TcpLogActivity : AppCompatActivity() {

    companion object {
        var isPaused: Boolean = false
        var listNetMsgType: MutableList<NetMsgType> = ArrayList()
        private var handler : MyHandler? = null

        fun addRec(rec: String) {
            addMsg(0, rec)
        }

        fun addSend(send: String) {
            addMsg(1, send)
        }

        private fun addMsg(type: Int, msg: String) {
            if (isPaused) {
                return
            }
            if (listNetMsgType.size > 100) {
                listNetMsgType.removeAt(0)
            }
            val netMsgType = NetMsgType()
            netMsgType.type = type
            netMsgType.msg = msg
            val dft = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
            netMsgType.time = dft.format(Date())
            listNetMsgType.add(netMsgType)
            if(null != handler) {
                handler!!.obtainMessage().sendToTarget()
            }
        }
    }

    private lateinit var adapterMsg: AdapterBridgeMsg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp_log)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        handler = MyHandler(this)
        setListAdapter()
        isPaused = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tcp_log, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish() // back button
                return true
            }
            R.id.menu_pause -> {
                isPaused = !isPaused
                if (isPaused) {
                    item.title = "启动"
                } else {
                    item.title = "暂停"
                }
            }
            R.id.menu_clean -> {
                listNetMsgType.clear()
                adapterMsg.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler = null
    }

    private fun setListAdapter() {
        adapterMsg = AdapterBridgeMsg(this, listNetMsgType)
        lvMsg.adapter = adapterMsg
    }

    class MyHandler(activity : TcpLogActivity) : Handler(){
        private var mActivity = WeakReference<TcpLogActivity>(activity)

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (!isPaused) {
                mActivity.get()!!.adapterMsg.notifyDataSetChanged()
            }
        }
    }

}
