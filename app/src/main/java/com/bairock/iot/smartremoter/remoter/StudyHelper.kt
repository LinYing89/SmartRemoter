package com.bairock.iot.smartremoter.remoter

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.data.RemoterKeyDao
import com.bairock.iot.smartremoter.zview.DragRemoterKeyButton

object StudyHelper {

    private fun createLayout(context: AppCompatActivity): View {
        val layout = context.layoutInflater
                .inflate(R.layout.layout_key_study, null)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return layout
    }

    /**
     * 显示学习对话框
     * v: 点击的控件
     */
    fun showPopUp(v: View, context: AppCompatActivity) {
        showPopUp(v, (v as DragRemoterKeyButton).remoterKey, context)
    }

    /**
     * 显示学习对话框
     * v: 点击的控件
     * key : 控件对应的按键对象
     */
    fun showPopUp(v: View, key: RemoterKey, context: AppCompatActivity) {
        showPopUp(v, key, false, context)
    }

    /**
     * 显示学习对话框
     * v: 点击的控件
     * key : 控件对应的按键对象
     * showRenameBtn : 对话框中是否显示重命名按钮, true为显示
     */
    fun showPopUp(v: View, key: RemoterKey, showRenameBtn: Boolean, context: AppCompatActivity) {
        val layout = createLayout(context)
        val btnRename = layout.findViewById(R.id.btnRename) as Button
        if (showRenameBtn) {
            btnRename.visibility = View.VISIBLE
        }
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWindow = initPop(layout, v, key, context)

        btnRename.setOnClickListener {
            popupWindow.dismiss()
            showRenameDialog(v, key, context)
        }
    }

    private fun initPop(layout: View, v: View, key: RemoterKey, context: AppCompatActivity): PopupWindow {
        val btnStudy = layout.findViewById(R.id.btnStudy) as Button

        val popupWindow = PopupWindow(layout)

        val width = popupWindow.contentView.measuredWidth
        val height = popupWindow.contentView.measuredHeight
        Log.e("pop", "w:$width, h:$height" )
        popupWindow.width = width
        popupWindow.height = height

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAsDropDown(v)
        btnStudy.setOnClickListener {
            popupWindow.dismiss()
            StudyKeyActivity.remoterKey = key
            context.startActivity(Intent(context, StudyKeyActivity::class.java))
        }
        return popupWindow
    }

    private fun showRenameDialog(v: View, remoterKey: RemoterKey, context: AppCompatActivity) {
        val editNewName = EditText(context)
        editNewName.setText(remoterKey.name)
        AlertDialog.Builder(context)
                .setTitle("输入按键名称")
                .setView(editNewName)
                .setPositiveButton("确定"
                ) { _, _ ->
                    val value = editNewName.text.toString()
                    if (remoterKey.remoter.keyNameIsExists(value)) {
                        Toast.makeText(context, "名称重复", Toast.LENGTH_SHORT).show()
                    } else {
                        remoterKey.name = value
                        (v as Button).text = value
                        RemoterKeyDao.get(context).update(remoterKey)
                    }
                }.setNegativeButton("取消", null).create().show()
    }
}