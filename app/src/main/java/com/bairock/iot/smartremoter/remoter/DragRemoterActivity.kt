package com.bairock.iot.smartremoter.remoter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.Constant
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.zview.DragRemoterKeyButton
import kotlinx.android.synthetic.main.activity_drag_remoter.*

class DragRemoterActivity : AppCompatActivity() {

    private var listDragRemoterBtn = mutableListOf<DragRemoterKeyButton>()
    lateinit var remoter : Remoter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_remoter)
        val coding = intent.getStringExtra("coding")
        remoter = HamaApp.DEV_GROUP.findDeviceWithLongCoding(coding) as Remoter
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        initListButtons()
        setGridView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initListButtons() {
        for (remoterKey in remoter.listRemoterKey) {
            createAndAddDragRemoterButton(remoterKey)
        }
    }

    private fun createDragRemoterButton(remoterKey : RemoterKey) : DragRemoterKeyButton{
        val rb = DragRemoterKeyButton(this)
        rb.remoterKey = remoterKey
        return rb
    }

    private fun createAndAddDragRemoterButton(remoterKey : RemoterKey){
        val rb = createDragRemoterButton(remoterKey)
        rb.setOnLongClickListener { p0 ->
            StudyHelper.showPopUp(p0, this)
//            showPopUp(p0)
            false
        }
        rb.setOnClickListener{
            val remoterKey1 = (it as DragRemoterKeyButton).remoterKey
            HamaApp.sendOrder(remoterKey1.remoter.parent, remoterKey1.createCtrlKeyOrder(), true)
        }
        listDragRemoterBtn.add(rb)
    }

    private fun setGridView() {
        layoutRoot.removeAllViews()
        for (cb in listDragRemoterBtn) {
            addToLayout(cb)
        }
    }

    private fun addToLayout(cb: DragRemoterKeyButton) {
        val width = Constant.getRemoterKeyWidth()
        val layoutParams = RelativeLayout.LayoutParams(
                width, width)
        if (cb.remoterKey.locationY >= Constant.displayHeight - width) {
            cb.remoterKey.locationY =  Constant.displayHeight - width
        }
        if (cb.remoterKey.locationX >=  Constant.displayWidth - width) {
            cb.remoterKey.locationX =  Constant.displayWidth - width
        }

        layoutParams.topMargin = cb.remoterKey.locationY
        layoutParams.leftMargin = cb.remoterKey.locationX
        cb.layoutParams = layoutParams
        layoutRoot.addView(cb)
    }

    private fun showPopUp(v: View) {
        val layout = this.layoutInflater
                .inflate(R.layout.layout_key_study, null)
        val btnStudy = layout.findViewById(R.id.btnStudy) as Button

        val rb = v as DragRemoterKeyButton
        val popupWindow = PopupWindow(layout, Constant.dip2px(108f),  Constant.dip2px(70f))

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        val location = IntArray(2)
//        v.getLocationOnScreen(location)

        popupWindow.showAsDropDown(v)
        btnStudy.setOnClickListener {
            popupWindow.dismiss()
            StudyKeyActivity.remoterKey = rb.remoterKey
            startActivity(Intent(this, StudyKeyActivity::class.java))
        }
    }
}
