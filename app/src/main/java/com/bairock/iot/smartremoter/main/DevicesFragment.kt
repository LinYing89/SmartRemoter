package com.bairock.iot.smartremoter.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bairock.iot.smartremoter.R
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration
import kotlinx.android.synthetic.main.fragment_devices.*

class DevicesFragment : BaseFragment() {

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

    private val onMenuItemClickListener = SwipeMenuItemClickListener{
        it.closeMenu()
        val adapterPosition = it.adapterPosition
//        val device = listShowDevices.get(adapterPosition)
//        IntelDevHelper.OPERATE_DEVICE = device
        when(it.position) {
            0 ->{}
            1 ->{
                android.app.AlertDialog.Builder(this.context)
                        .setMessage("确定删除吗")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定"
                        ) { _, _ ->

                        }.show()
            }
        }
    }

    private val onItemClickListener = SwipeItemClickListener{ view: View, i: Int ->

    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DevicesFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
