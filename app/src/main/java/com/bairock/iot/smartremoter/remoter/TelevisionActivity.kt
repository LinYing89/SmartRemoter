package com.bairock.iot.smartremoter.remoter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import com.bairock.iot.intelDev.device.remoter.Remoter
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.media.Media
import kotlinx.android.synthetic.main.activity_television.*

class TelevisionActivity : AppCompatActivity() {

    lateinit var remoter: Remoter
    private var keys = arrayOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_television)

        val coding = intent.getStringExtra("coding")
        remoter = HamaApp.DEV_GROUP.findDeviceWithLongCoding(coding) as Remoter

        keys = arrayOf(btnMute, btnPower, btnKey1, btnKey2, btnKey3, btnKey4, btnKey5,
                btnKey6, btnKey7, btnKey8, btnKey9, btnKey0, btnKeyBack, btnKeyTvAv, btnVoiceLeft,
                btnVoiceRight, btnLeft, btnTop, btnRight, btnDown, btnOk, btnKeyExtend1, btnKeyExtend2,
                btnKeyExtend3, btnKeyExtend4, btnKeyExtend5, btnKeyExtend6, btnKeyExtend7, btnKeyExtend8)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = remoter.name
        }

        for (btn in keys) {
            btn.setOnTouchListener(onTouchListener)
            btn.setOnClickListener(onClickListener)
            btn.setOnLongClickListener(onLongClickListener)
        }

        for(i in 21 until keys.size - 1){
            val btn = keys[i]
            val key = remoter.findKeyByNumber((i + 1).toString())
            btn.text = key.name
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private val onTouchListener = View.OnTouchListener { p0, p1 ->
        when (p0.id) {
            R.id.btnLeft, R.id.btnTop, R.id.btnRight, R.id.btnDown -> {
                imgCtrl(p1.action, p0.id)
            }
            R.id.btnOk -> {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imgOk.setImageResource(R.drawable.key_ok_pressed)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        imgOk.setImageResource(R.drawable.key_ok)
                    }
                }
            }
            R.id.btnVoiceLeft -> {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imgVoice.setImageResource(R.drawable.ctrl_voice_left)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        imgVoice.setImageResource(R.drawable.ctrl_voice)
                    }
                }
            }
            R.id.btnVoiceRight -> {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imgVoice.setImageResource(R.drawable.ctrl_voice_right)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        imgVoice.setImageResource(R.drawable.ctrl_voice)
                    }
                }
            }
            R.id.btnMute -> {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imgMute.setImageResource(R.drawable.mute_pressed)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        imgMute.setImageResource(R.drawable.mute)
                    }
                }
            }
            R.id.btnPower -> {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        imgPower.setImageResource(R.drawable.power_pressed)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        imgPower.setImageResource(R.drawable.power)
                    }
                }
            }
            else -> {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.setBackgroundResource(R.drawable.key_bg_pressed)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        p0.setBackgroundResource(R.drawable.key_bg)
                    }
                }

            }
        }
        false
    }

    private val onClickListener = View.OnClickListener {
        Media.playCtrlRing()
        val index = keys.indexOf(it)
        val key = remoter.findKeyByNumber((index + 1).toString())
        HamaApp.sendOrder(key.remoter.parent, key.createTestKeyOrder(), true)
    }

    private val onLongClickListener = View.OnLongClickListener {
        val index = keys.indexOf(it) + 1
        if(index >= 22){
            StudyHelper.showPopUp(it, remoter.findKeyByNumber(index.toString()), true, this)
        }else {
            StudyHelper.showPopUp(it, remoter.findKeyByNumber(index.toString()), this)
        }
        true
    }

    private fun imgCtrl(action: Int, id: Int) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                imgCtrlPressed(id)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                imgCtrlDefault()
            }
        }
    }

    private fun imgCtrlPressed(id: Int) {
        when (id) {
            R.id.btnLeft -> imgCtrl.setImageResource(R.drawable.ctrl_direct_left)
            R.id.btnTop -> imgCtrl.setImageResource(R.drawable.ctrl_direct_top)
            R.id.btnRight -> imgCtrl.setImageResource(R.drawable.ctrl_direct_right)
            R.id.btnDown -> imgCtrl.setImageResource(R.drawable.ctrl_direct_down)
        }
    }

    private fun imgCtrlDefault() {
        imgCtrl.setImageResource(R.drawable.ctrl_direct)
    }

}
