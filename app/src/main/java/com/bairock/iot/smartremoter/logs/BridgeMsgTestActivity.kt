package com.bairock.iot.smartremoter.logs

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.logs.BridgeState.OnDevCodingChangedListener
import kotlinx.android.synthetic.main.activity_bridge_msg_test.*
import java.lang.ref.WeakReference

class BridgeMsgTestActivity : AppCompatActivity() {

    companion object {
        var bridgeState: BridgeState? = null
    }

    var myHandler: MyHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge_msg_test)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            if (bridgeState!!.getDevCoding() != null) {
                actionBar.title = bridgeState!!.getDevCoding()
            }
        }

        val sb = StringBuilder()
        for (netMsgType in bridgeState!!.listBridgeMsgType) {
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
        //sTextSpannable.setSpan(new ForegroundColorSpan(Color.RED),1,4,0);

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

        myHandler = MyHandler(this)

        bridgeState!!.onCollectionChangedMsgListener = object : BridgeState.OnCollectionChangedMsgListener {
            override fun onAddMsg(netMsgType: NetMsgType) {
                if (null != myHandler) {
                    val type: String = if (netMsgType.type == 0) {
                        " <- "
                    } else {
                        " -> "
                    }
                    val str = type + netMsgType.time + " " + netMsgType.msg
                    myHandler!!.obtainMessage(0, str).sendToTarget()
                }
            }

            override fun onRemovedMsg(netMsgType: NetMsgType) {}

        }

        bridgeState!!.onDevCodingChangedListener = object : OnDevCodingChangedListener {
            override fun onDevCodingChanged() {
                myHandler!!.obtainMessage(1).sendToTarget()
            }
        }

    }

    class MyHandler(activity: BridgeMsgTestActivity) : Handler() {
        private var mActivity = WeakReference<BridgeMsgTestActivity>(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val theActivity = mActivity.get()
            when (msg.what) {
                0 -> {

                    val str = msg.obj.toString() + "\n"
                    if (str.contains("<")) {
                        val sTextSpannable = SpannableString(str)
                        sTextSpannable.setSpan(ForegroundColorSpan(Color.RED), 1, str.length, 0)
                        //                        theActivity.tvLogs.setText(sTextSpannable);
                        theActivity!!.tvLogs!!.append(sTextSpannable)
                    } else {
                        theActivity!!.tvLogs!!.append(msg.obj.toString() + "\n")
                    }
                }
                1 -> if (theActivity!!.supportActionBar != null) {
                    theActivity.supportActionBar!!.title = bridgeState!!.getDevCoding()
                }
            }
        }
    }
}
