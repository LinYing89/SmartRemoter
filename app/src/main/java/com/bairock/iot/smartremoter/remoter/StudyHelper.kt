package com.bairock.iot.smartremoter.remoter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.Constant
import com.bairock.iot.smartremoter.zview.DragRemoterKeyButton

class StudyHelper {

    companion object {
        fun showPopUp(v: View, context: AppCompatActivity) {
            showPopUp(v, (v as DragRemoterKeyButton).remoterKey, context)
        }

        fun showPopUp(v: View, key : RemoterKey, context: AppCompatActivity) {
            val layout = context.layoutInflater
                    .inflate(R.layout.layout_key_study, null)
            val btnStudy = layout.findViewById(R.id.btnStudy) as Button

            val popupWindow = PopupWindow(layout, Constant.dip2px(108f),  Constant.dip2px(70f))

            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.showAsDropDown(v)
            btnStudy.setOnClickListener {
                popupWindow.dismiss()
                StudyKeyActivity.remoterKey = key
                context.startActivity(Intent(context, StudyKeyActivity::class.java))
            }
        }
    }

}