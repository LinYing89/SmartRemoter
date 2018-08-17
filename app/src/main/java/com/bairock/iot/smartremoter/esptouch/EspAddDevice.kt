package com.bairock.iot.smartremoter.esptouch

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.bairock.iot.intelDev.communication.DevServer
import com.bairock.iot.intelDev.communication.UdpServer
import com.bairock.iot.intelDev.device.CtrlModel
import com.bairock.iot.intelDev.device.DevStateHelper
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.OrderHelper
import com.bairock.iot.intelDev.linkage.LinkageTab
import com.bairock.iot.intelDev.user.DevGroup
import com.bairock.iot.intelDev.user.IntelDevHelper
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.DeviceDao
import com.bairock.iot.smartremoter.esptouch.task.EsptouchTask
import com.bairock.iot.smartremoter.esptouch.task.IEsptouchResult
import com.bairock.iot.smartremoter.esptouch.task.IEsptouchTask
import com.bairock.iot.smartremoter.settings.ConfigResult
import java.lang.ref.WeakReference

class EspAddDevice(private val context: Context) {

    companion object {
        var DEVICE: Device? = null
        var RECEIVED_OK_COUNT: Int = 0
        var CONFIGING: Boolean = false
        //=ture表示使用udp+tcp方式配置设备，=false表示使用纯udp方式配置设备
        private val TCP_CONFIG_MODEL = true
    }

    private var configOk: Boolean = false
    private var mWifiAdmin = EspWifiAdminSimple(context)
    private var mProgressDialog: ProgressDialog? = null
    private var handler : Handler? = null

    private val moniAdd = false

    private fun getSsid(): String {
        var ssid = mWifiAdmin.wifiConnectedSsid
        if (ssid == null) {
            ssid = ""
        }
        return ssid
    }

    fun startConfig(psd: String, handler: Handler) {
        this.handler = handler
        CONFIGING = true
        DEVICE = null
        RECEIVED_OK_COUNT = 0
        if (moniAdd) {
            ConfigDeviceTask(this@EspAddDevice).execute()
            //showConfigProgress(null)
        } else {
            val apBssid = mWifiAdmin.wifiConnectedBssid
            //Boolean isSsidHidden = false;
            //String isSsidHiddenStr = "NO";
            val isSsidHiddenStr = "YES"
            val taskResultCountStr = Integer.toString(1)
            //                if (isSsidHidden){
            //                    isSsidHiddenStr = "YES";
            //                }
            EsptouchAsyncTask3(this).execute(getSsid(), apBssid, psd,
                    isSsidHiddenStr, taskResultCountStr)
        }
    }

    private class EsptouchAsyncTask3(activity: EspAddDevice) : AsyncTask<String, Void, List<IEsptouchResult>>() {

        internal var mActivity = WeakReference(activity)

        private var mEsptouchTask: IEsptouchTask? = null
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private val mLock = Any()

        init {
            mActivity = WeakReference(activity)
        }

        override fun onPreExecute() {
//            val theActivity = mActivity.get()
//            theActivity!!.showConfigProgress(mEsptouchTask)
        }

        override fun doInBackground(vararg params: String): List<IEsptouchResult> {
            val theActivity = mActivity.get()
            var taskResultCount = 0
            synchronized(mLock) {
                val apSsid = params[0]
                val apBssid = params[1]
                val apPassword = params[2]
                val isSsidHiddenStr = params[3]
                val taskResultCountStr = params[4]
                var isSsidHidden = false
                if (isSsidHiddenStr == "YES") {
                    isSsidHidden = true
                }
                taskResultCount = Integer.parseInt(taskResultCountStr)
                mEsptouchTask = EsptouchTask(apSsid, apBssid, apPassword,
                        isSsidHidden, theActivity!!.context)
            }
            return mEsptouchTask!!.executeForResults(taskResultCount)
        }

        override fun onPostExecute(result: List<IEsptouchResult>) {
            val theActivity = mActivity.get()
            val firstResult = result[0]
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled) {
                //int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                //final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc) {
                    for (resultInList in result) {
                        val ip = resultInList.inetAddress.hostAddress
                        if (!TextUtils.isEmpty(ip)) {
                            if (CONFIGING) {
                                ConfigDeviceTask(theActivity!!).execute()
                            }
                            //configResult(true);
                        }
                    }
                } else {
                    theActivity!!.configResult(ConfigResult.NET_ERROR)
                }
            }
        }
    }

    private val mLock = Any()
    private fun showConfigProgress(mEsptouchTask: IEsptouchTask?) {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setMessage("正在配置，请稍等...")
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.setOnCancelListener { dialog ->
            synchronized(mLock) {
                Log.i("EsptouchActivity", "progress dialog is canceled")
                CONFIGING = false
                mEsptouchTask?.interrupt()
            }
        }
        mProgressDialog!!.max = 100
        mProgressDialog!!.setTitle("配置设备,网络名称:" + getSsid())
        mProgressDialog!!.setIcon(R.drawable.ic_zoom_in_pink_24dp)
        mProgressDialog!!.setButton(DialogInterface.BUTTON_POSITIVE,
                "稍等...") { _, _ ->
            if (configOk) {
                DEVICE = null
                //finish();
            }
            mProgressDialog!!.dismiss()
        }
        mProgressDialog!!.show()
        mProgressDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
    }

    private class ConfigDeviceTask(activity: EspAddDevice) : AsyncTask<Void, Int, Boolean>() {

        var mActivity= WeakReference(activity)

        override fun doInBackground(vararg params: Void): Boolean? {
            try {
                //发送寻找正在配置设备的报文
                val ip = IntelDevHelper.getLocalIp()
                val seekOrder = getSeekOrder(ip, DevServer.PORT)
                var count = 1
                //publishProgress(0, 0)
                do {
                    if (count >= 10) {
                        return false
                    }
                    UdpServer.getIns().send(seekOrder)
                    Thread.sleep(5000)
                    //publishProgress(count * 10)
                    Log.e("EsptouchAct", "DEVICE" + DEVICE!!)
                    count++
                } while (DEVICE == null && CONFIGING)
                if (null == DEVICE) {
                    return false
                }
                if (TCP_CONFIG_MODEL) {
                    //publishProgress(100, 1)
                    return true
                }

                //下面的是纯udp方式时执行的代码
                Log.e("EsptouchAct", "DEVICE state id " + DEVICE!!.getDevStateId())
                //发送收到设备编码成功信息
                count = 1
                val deviceCodingOrder = getDeviceConfigOkOrder()
                //publishProgress(50, 1)
                do {
                    if (count >= 6 && DEVICE!!.getDevStateId() == DevStateHelper.CONFIGING) {
                        return false
                    }
                    UdpServer.getIns().send(deviceCodingOrder)
                    //DevChannelBridgeHelper.getIns().sendDevOrder(DEVICE, deviceCodingOrder);
                    Log.e("EsptouchAct", "send config ok order $deviceCodingOrder")
                    //UdpMsgSender.getIns().send(deviceCodingOrder);
                    Thread.sleep(5000)
                    //publishProgress(50 + count * 8)
                    count++
                } while (DEVICE!!.devStateId == DevStateHelper.CONFIGING && CONFIGING && RECEIVED_OK_COUNT < 2)
                if (RECEIVED_OK_COUNT >= 2) {
                    //至少收到2次回复，配置成功，再发5次
                    for (i in 0..4) {
                        UdpServer.getIns().send(deviceCodingOrder)
                        Thread.sleep(1000)
                    }
                }
                Log.e("EsptouchAct", "config ok")
                //publishProgress(100)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            val theActivity = mActivity.get()
            theActivity!!.mProgressDialog!!.progress = values[0]!!
            if (values.size >= 2) {
                if (values[1] != null) {
                    if (values[1] == 1) {
                        theActivity.mProgressDialog!!.setMessage("配置成功的设备:" + DEVICE!!.coding)
                    }
                }
            }
        }

        override fun onPostExecute(success: Boolean?) {
            val theActivity = mActivity.get()!!
            if (success!!) {
                val device = DEVICE
                device!!.ctrlModel = CtrlModel.LOCAL
                val device2 = HamaApp.DEV_GROUP.findDeviceWithCoding(device.coding)
                if (device2 == null) {
                    //配置成功，保存到数据库
                    device.devGroup = HamaApp.DEV_GROUP
                    //先添加到数据库，后添加到用户组，因为添加到数据库后，如果数据库中已有设备的数据信息
                    //则会读取数据信息进行赋值
                    DeviceDao.get(HamaApp.HAMA_CONTEXT).add(device)

                    HamaApp.DEV_GROUP.addDevice(device)
                    //设置设备状态改变监听器
                    HamaApp.setDeviceListener(device)
                    device.devStateId = DevStateHelper.DS_ZHENG_CHANG

                    //添加到连锁内存表
                    val listIStateDev = DevGroup.findListIStateDev(device, true)
                    for (device1 in listIStateDev) {
                        LinkageTab.getIns().addTabRow(device1)
                    }
                }
                val result = ConfigResult.SUCCESS
                result.msg += ", ${device.coding}"
                theActivity.configResult(ConfigResult.SUCCESS)
            } else {
                //设备无返回，配置失败
                theActivity.configResult(ConfigResult.DEVICE_ERROR)
            }
        }

        override fun onCancelled() {

        }

        private fun getSeekOrder(ip: String, port: Int): String {
            val order = "S0:n$ip,$port:+"
            return OrderHelper.getOrderMsg(order)
        }

        private fun getDeviceConfigOkOrder(): String {
            val order = "S" + DEVICE!!.coding + ":+ok"
            return OrderHelper.getOrderMsg(order)
        }
    }

    private fun configResult(result : ConfigResult) {
        CONFIGING = false
        handler!!.obtainMessage(0, result)
//        mProgressDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = true
//        mProgressDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).text = "确定"
//        configOk = if (result) {
//            mProgressDialog!!.setMessage("配置成功,设备编码:$message")
//            mProgressDialog!!.setIcon(R.drawable.ic_check_pink_24dp)
//            true
//        } else {
//            mProgressDialog!!.setMessage("配置失败:$message")
//            mProgressDialog!!.setIcon(R.drawable.ic_close_pink_24dp)
//            false
//        }
        DEVICE = null
        RECEIVED_OK_COUNT = 0
    }
}