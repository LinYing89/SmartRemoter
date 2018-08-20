package com.bairock.iot.smartremoter.settings

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.Constant
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.RemoterKeyDao
import com.bairock.iot.smartremoter.zview.DragRemoterKeyButton
import kotlinx.android.synthetic.main.activity_drag_remote_set_layout.*

class DragRemoteSetLayoutActivity : AppCompatActivity(), View.OnTouchListener {

    lateinit var remoter : Remoter

    private var listDragRemoterBtn = mutableListOf<DragRemoterKeyButton>()
    private var lastX = 0
    private var lastY = 0
    private var longClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_remote_set_layout)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_custom_key, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_add_key -> {
                showRenameDialog(null)
            }
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
        rb.setOnTouchListener(this)
        return rb
    }

    private fun createAndAddDragRemoterButton(remoterKey : RemoterKey){
        val rb = createDragRemoterButton(remoterKey)
        rb.setOnLongClickListener { p0 ->
            if(longClick){
                showPopUp(p0)
            }
            false
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

    private fun showRenameDialog(dragRemoterKeyButton: DragRemoterKeyButton?) {
        val editNewName = EditText(this)
        if(null != dragRemoterKeyButton) {
            editNewName.setText(dragRemoterKeyButton.text)
        }
        AlertDialog.Builder(this)
                .setTitle("输入按键名称")
                .setView(editNewName)
                .setPositiveButton("确定"
                ) { _, _ ->
                    val value = editNewName.text.toString()
                    if(remoter.keyNameIsExists(value)){
                        Toast.makeText(this, "名称重复", Toast.LENGTH_SHORT).show()
                    }else{
                        if(null == dragRemoterKeyButton) {
                            val num = remoter.nextNumber()
                            if (null != num) {
                                val rk = RemoterKey()
                                rk.number = num
                                rk.name = value
                                rk.locationX = 10
                                rk.locationY = 10
                                remoter.addRemoterKey(rk)
                                RemoterKeyDao.get(this).add(rk)
                                createAndAddDragRemoterButton(rk)
                                addToLayout(listDragRemoterBtn.last())
                            }
                        }else{
                            dragRemoterKeyButton.remoterKey.name = value
                            dragRemoterKeyButton.text = value
                            RemoterKeyDao.get(this).update(dragRemoterKeyButton.remoterKey)
                        }
                    }
                }.setNegativeButton("取消", null).create().show()
    }

    private fun showPopUp(v: View) {
        val layout = this.layoutInflater
                .inflate(R.layout.edit_del, null)
        val btnEdit = layout.findViewById(R.id.btnEdit) as Button
        val btnDel = layout.findViewById(R.id.btnDel) as Button

        val popupWindow = PopupWindow(layout, Constant.dip2px(120f),
                Constant.dip2px(120f))

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val location = IntArray(2)
        v.getLocationOnScreen(location)

        popupWindow.showAsDropDown(v)
        btnEdit.setOnClickListener {
            showRenameDialog(v as DragRemoterKeyButton)
            popupWindow.dismiss()
        }
        btnDel.setOnClickListener {
            popupWindow.dismiss()
            deleteBtn(v as DragRemoterKeyButton)
        }
    }

    private fun deleteBtn(dragRemoterKeyButton: DragRemoterKeyButton){
        RemoterKeyDao.get(this).delete(dragRemoterKeyButton.remoterKey)
        remoter.removeRemoterKey(dragRemoterKeyButton.remoterKey)
        listDragRemoterBtn.remove(dragRemoterKeyButton)
        layoutRoot.removeView(dragRemoterKeyButton)
    }

    override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
        val dragRemoterKeyButton = p0 as DragRemoterKeyButton
        when (p1.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = p1.rawX.toInt()
                lastY = p1.rawY.toInt()
                longClick = true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = p1.rawX.toInt() - lastX
                val dy = p1.rawY.toInt() - lastY
                if (dx > 1 || dy > 1) {
                    longClick = false
                }

                var top = p0.getTop() + dy
                var left = p0.getLeft() + dx

                if (top <= 0) {
                    top = 0
                }
                if (top >= Constant.displayHeight - dragRemoterKeyButton.height) {
                    top =  Constant.displayHeight - dragRemoterKeyButton.height
                }
                if (left >=  Constant.displayWidth - dragRemoterKeyButton.width) {
                    left =  Constant.displayWidth - dragRemoterKeyButton.width
                }
                if (left <= 0) {
                    left = 0
                }

                dragRemoterKeyButton.remoterKey.locationX = left
                dragRemoterKeyButton.remoterKey.locationY = top

                val width = Constant.getRemoterKeyWidth()
                val layoutParams = RelativeLayout.LayoutParams(
                        width, width)
                layoutParams.topMargin = dragRemoterKeyButton.remoterKey.locationY
                layoutParams.leftMargin = dragRemoterKeyButton.remoterKey.locationX
                dragRemoterKeyButton.layoutParams = layoutParams

//                (dragRemoterKeyButton.layoutParams as RelativeLayout.LayoutParams).topMargin = top
//                (dragRemoterKeyButton.layoutParams as RelativeLayout.LayoutParams).leftMargin = left
                //dragRemoterKeyButton.layoutBtn()
                // v.layout(left, top, left + iv.getWidth(), top + iv.getHeight());
                lastX = p1.rawX.toInt()
                lastY = p1.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                if(!longClick){
                    RemoterKeyDao.get(this).update(dragRemoterKeyButton.remoterKey)
                }
                //setGridView()
            }
        }
        return false
    }
}
