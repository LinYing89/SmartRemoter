package com.bairock.iot.smartremoter.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bairock.iot.intelDev.device.Coordinator
import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignalContainer
import com.bairock.iot.intelDev.device.devswitch.DevSwitch
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.intelDev.device.remoter.RemoterContainer
import com.bairock.iot.intelDev.user.IntelDevHelper

import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.adapter.AdapterDevices
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.DeviceDao
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration
import kotlinx.android.synthetic.main.fragment_devices.*
import java.lang.ref.WeakReference

class DevicesFragment : BaseFragment() {

    private lateinit var listShowDevices: List<Device>
    private var rootDevice: Device? = null
    private var adapterDevices: AdapterDevices? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        fragment = 0
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeMenuRecyclerViewDevice.layoutManager = LinearLayoutManager(this.context)
        swipeMenuRecyclerViewDevice.addItemDecoration(DefaultItemDecoration(Color.LTGRAY))
        swipeMenuRecyclerViewDevice.setSwipeMenuCreator(swipeMenuConditionCreator)
        swipeMenuRecyclerViewDevice.setSwipeMenuItemClickListener(onMenuItemClickListener)
        swipeMenuRecyclerViewDevice.setSwipeItemClickListener(onItemClickListener)
        setDeviceList(HamaApp.DEV_GROUP.listDevice)
        handler = MyHandler(this)

        btnAddRemoter.setOnClickListener {
            val intent = Intent(this.context!!, SelectRemoterActivity::class.java)
            startActivityForResult(intent, RESULT_CODE_SELECT_REMOTER)
        }
    }

    private fun setDeviceList(list: List<Device>) {
        listShowDevices = list
        adapterDevices = AdapterDevices(this.context!!, list)
        swipeMenuRecyclerViewDevice.adapter = adapterDevices

        when (rootDevice) {
            null ->{
                btnSearchDevice.visibility = View.GONE
                btnAddRemoter.visibility = View.GONE
                btnAddDevice.visibility = View.VISIBLE
            }
            is Coordinator -> {
                btnSearchDevice.visibility = View.VISIBLE
                btnAddRemoter.visibility = View.GONE
                btnAddDevice.visibility = View.GONE
            }
            is RemoterContainer -> {
                btnSearchDevice.visibility = View.GONE
                btnAddRemoter.visibility = View.VISIBLE
                btnAddDevice.visibility = View.GONE
            }
            else -> {
                btnSearchDevice.visibility = View.GONE
                btnAddRemoter.visibility = View.GONE
                btnAddDevice.visibility = View.GONE
            }
        }
    }

    private val swipeMenuConditionCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
        val width = resources.getDimensionPixelSize(R.dimen.dp_70)
        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        val modelItem = SwipeMenuItem(this.context)
                .setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.menu_blue))
                .setText("编辑")
                .setTextColor(Color.WHITE)
                .setWidth(width + 20)
                .setHeight(height)
        swipeRightMenu.addMenuItem(modelItem)

        val deleteItem = SwipeMenuItem(this.context)
                .setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(deleteItem)
    }

    private val onMenuItemClickListener = SwipeMenuItemClickListener {
        it.closeMenu()
        val adapterPosition = it.adapterPosition
        val device = listShowDevices[adapterPosition]
        IntelDevHelper.OPERATE_DEVICE = device
        when (it.position) {
            0 -> {
            }
            1 -> {
                android.app.AlertDialog.Builder(this.context)
                        .setMessage("确定删除吗")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定"
                        ) { _, _ ->

                        }.show()
            }
        }
    }

    private val onItemClickListener = SwipeItemClickListener { _: View, i: Int ->
        IntelDevHelper.OPERATE_DEVICE = listShowDevices[i]
        if (IntelDevHelper.OPERATE_DEVICE is DevSwitch || IntelDevHelper.OPERATE_DEVICE is DevCollectSignalContainer) {
//            ChildElectricalActivity.controller = IntelDevHelper.OPERATE_DEVICE as DevHaveChild
//            this.context!!.startActivity(Intent(this@SearchActivity, ChildElectricalActivity::class.java))
        } else if (IntelDevHelper.OPERATE_DEVICE is DevHaveChild) {
            nextPage()
        } else if (IntelDevHelper.OPERATE_DEVICE is Remoter) {
//            DragRemoteSetLayoutActivity.Companion.setREMOTER(IntelDevHelper.OPERATE_DEVICE as Remoter)
//            startActivity(Intent(this@SearchActivity, DragRemoteSetLayoutActivity::class.java))
        }
    }

    private fun nextPage() {
        listener!!.showBack(true)
        rootDevice = IntelDevHelper.OPERATE_DEVICE
        setDeviceList((rootDevice as DevHaveChild).listDev)
    }

    private fun previousPage() {
        val list = getRootDevices(rootDevice)
        setDeviceList(list)
    }

    private fun getRootDevices(rootDevice: Device?): List<Device> {
        if (null == rootDevice) {
            return HamaApp.DEV_GROUP.listDevice
        }
        val parent = rootDevice.parent
        this.rootDevice = parent
        return if (null == parent) {
            listener!!.showBack(false)
            HamaApp.DEV_GROUP.listDevice
        } else (parent as DevHaveChild).listDev
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE_SELECT_REMOTER) {
                //选择遥控器界面返回
                val code = data.getIntExtra("remoterCode", 1)
                val name = data.getStringExtra("remoterName")
                val rc = rootDevice as RemoterContainer
                val remoter = rc.createRemoter(code.toString())
                remoter.name = name + remoter.subCode
                rc.addChildDev(remoter)
                DeviceDao.get(this.context!!).add(remoter)
                adapterDevices!!.notifyDataSetChanged()
            }
        }
    }

    class MyHandler(fragment: DevicesFragment) : Handler() {
        private var mActivity = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val mFragment = mActivity.get()!!
            when (msg.what) {
                NEXT_PAGE -> {
                    mFragment.nextPage()
                }
                PREVIOUS_PAGE -> {
                    mFragment.previousPage()
                }
            }
        }
    }

    companion object {

        lateinit var handler: MyHandler
        const val NEXT_PAGE = 0

        /**
         * 子设备页转到子设备的父设备页
         */
        const val PREVIOUS_PAGE = 1
        /**
         * 选择遥控器界面响应编码
         */
        const val RESULT_CODE_SELECT_REMOTER = 2

        @JvmStatic
        fun newInstance() =
                DevicesFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
