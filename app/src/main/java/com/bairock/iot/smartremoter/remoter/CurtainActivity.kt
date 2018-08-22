package com.bairock.iot.smartremoter.remoter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.HamaApp
import kotlinx.android.synthetic.main.activity_curtain.*

class CurtainActivity : AppCompatActivity() {

    lateinit var remoter: Remoter
    private var keys = arrayOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curtain)

        keys = arrayOf(btnOpen, btnStop, btnClose)

        val coding = intent.getStringExtra("coding")
        remoter = HamaApp.DEV_GROUP.findDeviceWithLongCoding(coding) as Remoter

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = remoter.name
        }

        for (btn in keys) {
            btn.setOnClickListener(onClickListener)
            btn.setOnLongClickListener(onLongClickListener)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private val onClickListener = View.OnClickListener {
        val index = keys.indexOf(it)
        val key = remoter.listRemoterKey[index]
        HamaApp.sendOrder(key.remoter.parent, key.createTestKeyOrder(), true)
    }

    private val onLongClickListener = View.OnLongClickListener {
        StudyHelper.showPopUp(it, remoter.listRemoterKey[keys.indexOf(it)], this)
        true
    }

}
