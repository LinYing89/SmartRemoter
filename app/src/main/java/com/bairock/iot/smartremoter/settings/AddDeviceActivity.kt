package com.bairock.iot.smartremoter.settings

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.esptouch.EspAddDevice
import kotlinx.android.synthetic.main.activity_add_device.*
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

class AddDeviceActivity : AppCompatActivity() {

    var handler : MyHandler? = null

    private var configTask : ConfigProgressTask? = null
    private var espAddDevice : EspAddDevice = EspAddDevice(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        handler = MyHandler(this)
        btnConfig.setOnClickListener { it.visibility = View.GONE
            val psd = etxtPsd.text.toString()
            Config.setRoutePsd(this, psd)
            progressConfig.progress = 0
            progressConfig.visibility = View.VISIBLE
            configTask = ConfigProgressTask(this)
            configTask!!.executeOnExecutor(Executors.newCachedThreadPool())
            espAddDevice.startConfig(psd, handler!!)
        }

        etxtPsd.setText(Config.routePsd)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            setResult(Activity.RESULT_CANCELED, Intent())
            if(EspAddDevice.CONFIGING){
                return false
            }
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(EspAddDevice.CONFIGING){
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun stopConfig(){
        if(null != configTask){
            configTask!!.cancel(false)
        }
    }

    fun showResultDialog2(result : ConfigResult){
        stopConfig()
        progressConfig.visibility = View.GONE
        btnConfig.visibility = View.VISIBLE

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
        private var netOK = false

        override fun doInBackground(vararg p0: Void?): Boolean {
            var i = 0
            while (i < 200){
                Thread.sleep(200)

                if(i == 198){
                    netOK = true
                }

                if(netOK){
                    i = 200
                }else{
                    if(i < 198) {
                        i++
                    }
                }
                publishProgress(i)
                if(isCancelled){
                    return false
                }
            }
            while (i < 600){
                Log.e("AddDevAct", "dev $i?")
                Thread.sleep(100)
                if(i < 598) {
                    i++
                }
                publishProgress(i)
                if(isCancelled){
                    return false
                }
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
