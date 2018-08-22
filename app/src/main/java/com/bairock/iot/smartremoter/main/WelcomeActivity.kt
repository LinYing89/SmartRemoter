package com.bairock.iot.smartremoter.main

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import com.bairock.iot.intelDev.communication.*
import com.bairock.iot.intelDev.device.*
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer
import com.bairock.iot.intelDev.device.remoter.*
import com.bairock.iot.intelDev.linkage.LinkageHelper
import com.bairock.iot.intelDev.linkage.LinkageTab
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHelper
import com.bairock.iot.intelDev.linkage.timing.WeekHelper
import com.bairock.iot.intelDev.user.DevGroup
import com.bairock.iot.intelDev.user.User
import com.bairock.iot.smartremoter.MainActivity
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.Constant
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.app.HamaApp.Companion.setDeviceListener
import com.bairock.iot.smartremoter.app.LogUtils
import com.bairock.iot.smartremoter.communication.MyMessageAnalysiser
import com.bairock.iot.smartremoter.communication.MyOnCommunicationListener
import com.bairock.iot.smartremoter.data.DevGroupDao
import com.bairock.iot.smartremoter.data.SdDbHelper
import com.bairock.iot.smartremoter.data.UserDao
import com.bairock.iot.smartremoter.logs.MyOnBridgesChangedListener
import com.bairock.iot.smartremoter.logs.UdpLogActivity
import kotlinx.android.synthetic.main.activity_fullscreen.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * 欢迎页面
 */
class WelcomeActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        hideActionBar()
        val toMainTask = ToMainTask(this)
        toMainTask.execute()
    }

    private fun hideActionBar() {
        // Hide UI first
        supportActionBar?.hide()
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    class ToMainTask(activity: WelcomeActivity) : AsyncTask<Void, Void, Boolean>() {

        private var mActivity = WeakReference(activity)

        override fun doInBackground(vararg p0: Void?): Boolean {

            WeekHelper.ARRAY_WEEKS = arrayOf("日", "一", "二", "三", "四", "五", "六")
            initMainCodeInfo()

            //测试设备, 添加测试设备到数据库
            testRemoterContainer()

            init()

//            Thread.sleep(3000)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            val act = mActivity.get()
            act!!.startActivity(Intent(act, MainActivity::class.java))
            act.finish()
        }

        private fun init(){
            //获取屏幕宽高
            val displayMetrics = DisplayMetrics()
            mActivity.get()!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Constant.displayWidth = displayMetrics.widthPixels
            Constant.displayHeight = displayMetrics.heightPixels

            initUser()

            try {
                UdpServer.MY_PORT = 10010
                UdpServer.TO_PORT = 10011
                UdpServer.getIns().setUser(HamaApp.USER)
                UdpServer.getIns().run()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                HamaApp.DEV_SERVER = DevServer()
                HamaApp.DEV_SERVER!!.run()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            DevChannelBridge.analysiserName = MyMessageAnalysiser::class.java.name
            DevChannelBridgeHelper.getIns().stopSeekDeviceOnLineThread()
            DevChannelBridgeHelper.getIns().startSeekDeviceOnLineThread()

        }

        private fun initUser() {
            HamaApp.USER = SdDbHelper.getDbUser()
            HamaApp.DEV_GROUP = HamaApp.USER.listDevGroup[0]

            //本地tcp网络数据监听器名称
            //调试模式下监听网络数据
            if (LogUtils.APP_DBG) {
                FindDevHelper.getIns().onSendListener = object : FindDevHelper.OnSendListener {
                    override fun create() {

                    }

                    override fun send(s: String) {
                        UdpLogActivity.addSend(s)
                    }
                }

                DevChannelBridgeHelper.BRIDGE_COMMUNICATION_LISTENER_NAME = MyOnCommunicationListener::class.java.name
                DevChannelBridgeHelper.getIns().setOnBridgesChangedListener(MyOnBridgesChangedListener())
            }

            for (device in HamaApp.DEV_GROUP.listDevice) {
                //刚打开软件开始寻找设备
                FindDevHelper.getIns().findDev(device.coding)
                device.devStateId = DevStateHelper.DS_YI_CHANG
                setDeviceListener(device)
            }

            DevChannelBridgeHelper.getIns().user = HamaApp.USER

            //将状态设备添加到联动表，开始检查所有设备的联动和挡位状态
            val list = HamaApp.DEV_GROUP.findListIStateDev(true)
            LinkageTab.getIns().listLinkageTabRow.clear()
            for (device in list) {
                LinkageTab.getIns().addTabRow(device)
            }

            LinkageHelper.getIns().chain = HamaApp.DEV_GROUP.chainHolder
            LinkageHelper.getIns().loop = HamaApp.DEV_GROUP.loopHolder
            LinkageHelper.getIns().timing = HamaApp.DEV_GROUP.timingHolder

            GuaguaHelper.getIns().guaguaHolder = HamaApp.DEV_GROUP.guaguaHolder

            //GuaguaHelper.getIns().startCheckGuaguaThread();
        }

        private fun initMainCodeInfo() {
            val map = HashMap<String, String>()
            map[MainCodeHelper.XIE_TIAO_QI] = "协调器"
            map[MainCodeHelper.GUAGUA_MOUTH] = "呱呱嘴"
            map[MainCodeHelper.MEN_JIN] = "门禁"
            map[MainCodeHelper.YE_WEI] = "液位计"
            map[MainCodeHelper.COLLECTOR_SIGNAL] = "信号采集器"
            map[MainCodeHelper.COLLECTOR_SIGNAL_CONTAINER] = "多功能信号采集器"
            map[MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER] = "多功能气候采集器"
            map[MainCodeHelper.YAN_WU] = "烟雾探测器"
            map[MainCodeHelper.WEN_DU] = "温度"
            map[MainCodeHelper.SHI_DU] = "湿度"
            map[MainCodeHelper.JIA_QUAN] = "甲醛"
            map[MainCodeHelper.KG_1LU_2TAI] = "一路开关"
            map[MainCodeHelper.KG_2LU_2TAI] = "两路开关"
            map[MainCodeHelper.KG_3LU_2TAI] = "三路开关"
            map[MainCodeHelper.KG_XLU_2TAI] = "多路开关"
            map[MainCodeHelper.KG_3TAI] = "三态开关"
            map[MainCodeHelper.YAO_KONG] = "遥控器"
            map[MainCodeHelper.CHA_ZUO] = "插座"
            map[MainCodeHelper.SMC_WU] = "未知"
            map[MainCodeHelper.SMC_REMOTER_CHUANG_LIAN] = "窗帘"
            map[MainCodeHelper.SMC_REMOTER_DIAN_SHI] = "电视"
            map[MainCodeHelper.SMC_REMOTER_KONG_TIAO] = "空调"
            map[MainCodeHelper.SMC_REMOTER_TOU_YING] = "投影仪"
            map[MainCodeHelper.SMC_REMOTER_MU_BU] = "投影幕布"
            map[MainCodeHelper.SMC_REMOTER_SHENG_JIANG_JIA] = "升降架"
            map[MainCodeHelper.SMC_REMOTER_ZI_DING_YI] = "自定义"
            map[MainCodeHelper.SMC_DENG] = "灯"
            map[MainCodeHelper.SMC_CHUANG_HU] = "窗帘"
            map[MainCodeHelper.SMC_FA_MEN] = "阀门"
            map[MainCodeHelper.SMC_BING_XIANG] = "冰箱"
            map[MainCodeHelper.SMC_XI_YI_JI] = "洗衣机"
            map[MainCodeHelper.SMC_WEI_BO_LU] = "微波炉"
            map[MainCodeHelper.SMC_YIN_XIANG] = "音箱"
            map[MainCodeHelper.SMC_SHUI_LONG_TOU] = "水龙头"

            MainCodeHelper.getIns().setManCodeInfo(map)
        }

        private fun testRemoterContainer() {
            val user = User()
            user.name = "test123"
            user.psd = "a123456"
            val userDao = UserDao.get(HamaApp.HAMA_CONTEXT)
            userDao.clean()
            userDao.addUser(user)

            val devGroup = DevGroup("1", "a123", "g1")
            devGroup.id = 1
            user.addGroup(devGroup)
            val devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT)
            devGroupDao.clean()
            devGroupDao.add(devGroup)

            val remoterContainer = DeviceAssistent.createDeviceByMcId(MainCodeHelper.YAO_KONG, "9999") as RemoterContainer
            val r = DeviceAssistent.createDeviceByMc(MainCodeHelper.SMC_REMOTER_ZI_DING_YI, "1") as CustomRemoter
            val rk = RemoterKey("01", "1")
            r.addRemoterKey(rk)
            remoterContainer.addChildDev(r)

            val tv = DeviceAssistent.createDeviceByMc(MainCodeHelper.SMC_REMOTER_DIAN_SHI, "1") as Television
            remoterContainer.addChildDev(tv)

            val curtain = DeviceAssistent.createDeviceByMc(MainCodeHelper.SMC_REMOTER_CHUANG_LIAN, "1") as Curtain
            remoterContainer.addChildDev(curtain)
            devGroup.addDevice(remoterContainer)

            val coordinator = DeviceAssistent.createDeviceByMc(MainCodeHelper.XIE_TIAO_QI, "9999") as Coordinator
            val climate = DeviceAssistent.createDeviceByMc(MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER, "1") as DevCollectClimateContainer
            climate.findTemperatureDev().collectProperty.currentValue = 19.6f
            climate.findHumidityDev().collectProperty.currentValue = 60f
            coordinator.addChildDev(climate)
            devGroup.addDevice(coordinator)

            SdDbHelper.replaceDbUser(user)
        }
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}
