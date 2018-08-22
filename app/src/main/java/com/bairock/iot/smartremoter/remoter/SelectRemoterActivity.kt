package com.bairock.iot.smartremoter.remoter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.EditText
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.adapter.AdapterSelectRemoter
import kotlinx.android.synthetic.main.activity_select_remoter.*

/**
 * 选择遥控器设备
 */
class SelectRemoterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_remoter)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        lvRemotor.adapter = AdapterSelectRemoter(this)

        val remoterValues = resources.getStringArray(R.array.array_remoter_value)!!

        lvRemotor.onItemClickListener = AdapterView.OnItemClickListener { p0, _, p2, _ ->
            val intent = Intent()
            intent.putExtra("remoterCode", remoterValues[p2])
            intent.putExtra("remoterName", p0.getItemAtPosition(p2).toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
            //输入名称
//            showRenameDialog(remoterValues[p2], p0.getItemAtPosition(p2).toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setResult(Activity.RESULT_CANCELED, Intent())
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun showRenameDialog(remoterCode : String, remoterName : String) {
        val editNewName = EditText(this)
        editNewName.setText(remoterName)
        AlertDialog.Builder(this)
                .setTitle("重命名")
                .setView(editNewName)
                .setPositiveButton("确定"
                ) { _, _ ->
                    val intent = Intent()
                    intent.putExtra("remoterCode", remoterCode)
                    intent.putExtra("remoterName", remoterName)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }.setNegativeButton("取消", null).create().show()
    }
}
