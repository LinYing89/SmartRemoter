package com.bairock.iot.smartremoter.settings

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.esptouch.EspAddDevice
import kotlinx.android.synthetic.main.activity_add_device.*
import java.lang.ref.WeakReference

class AddDeviceActivity : AppCompatActivity() {

//    companion object {
//        var handler : MyHandler? = null
//    }

    var handler : MyHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        btnConfig.setOnClickListener { it.visibility = View.GONE
            progressConfig.progress = 0
            progressConfig.visibility = View.VISIBLE
            ConfigProgressTask(this).execute()
            EspAddDevice(this).startConfig("", handler!!)
        }
    }

    fun showResultDialog2(result : ConfigResult){
        val message = result.msg
        android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("确定"
                ) { _, _ ->
                    if(result == ConfigResult.SUCCESS){
                        finish()
                    }
                }.show()
    }

    class ConfigProgressTask(activity: AddDeviceActivity) : AsyncTask<Void, Int, Boolean>() {

        private var mActivity = WeakReference(activity)

        override fun doInBackground(vararg p0: Void?): Boolean {
            for (i in 1..600){
                publishProgress(i)
                Thread.sleep(100)
            }
            return true
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            mActivity.get()!!.progressConfig.progress = values[0]!!
        }
    }

    class MyHandler(activity: AddDeviceActivity) : Handler(){
        private var mActivity = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mActivity.get()!!.showResultDialog2(msg.obj as ConfigResult)
        }
    }
}
