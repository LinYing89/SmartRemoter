package com.bairock.iot.smartremoter.logs

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import com.bairock.iot.smartremoter.R
import kotlinx.android.synthetic.main.activity_bridge_msg_test.*
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

            if (null != handler) {
                val typeD: String = if (netMsgType.type == 0) {
                    " <- "
                } else {
                    " -> "
                }
                val str = typeD + netMsgType.time + " " + netMsgType.msg
                handler!!.obtainMessage(0, str).sendToTarget()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge_msg_test)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val sb = StringBuilder()
        for (netMsgType in listNetMsgType) {
            val type: String = if (netMsgType.type == 0) {
                " <- "
            } else {
                " -> "
            }
            sb.append(type)

            sb.append(netMsgType.time)
            sb.append(" ")
            sb.append(netMsgType.msg)
            sb.append("\n")
        }
        val text = sb.toString()
        val sTextSpannable = SpannableString(text)
        var index1: Int
        var index2 = 0
        while (true) {
            index1 = text.indexOf("<", index2)
            if (index1 == -1) {
                break
            }
            index2 = text.indexOf("\n", index1)
            if (index2 == -1) {
                break
            }
            sTextSpannable.setSpan(ForegroundColorSpan(Color.RED), index1, index2, 0)
        }
        tvLogs.text = sTextSpannable

        handler = MyHandler(this)
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
                tvLogs.text = ""
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler = null
    }

    class MyHandler(activity : TcpLogActivity) : Handler(){
        private var mActivity = WeakReference<TcpLogActivity>(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val act = mActivity.get()!!
            if (!isPaused) {
                val str = msg.obj.toString() + "\n"
                if (str.contains("<")) {
                    val sTextSpannable = SpannableString(str)
                    sTextSpannable.setSpan(ForegroundColorSpan(Color.RED), 1, str.length, 0)
                    //                        theActivity.tvLogs.setText(sTextSpannable);
                    act.tvLogs!!.append(sTextSpannable)
                } else {
                    act.tvLogs!!.append(msg.obj.toString() + "\n")
                }
            }
        }
    }

}
