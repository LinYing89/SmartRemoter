package com.bairock.iot.smartremoter.main

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.adapter.AdapterSelectRemoter
import kotlinx.android.synthetic.main.activity_select_remoter.*

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

        lvRemotor.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
            val intent = Intent()
            intent.putExtra("remoterCode", p2+1)
            intent.putExtra("remoterName", p0.getItemAtPosition(p2).toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
