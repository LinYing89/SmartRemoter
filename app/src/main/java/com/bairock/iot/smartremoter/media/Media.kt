package com.bairock.iot.smartremoter.media

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.SoundPool
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.settings.Config

object Media{

    private val soundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
    private val soundMap = HashMap<Int, Int>()

    fun init(context: Context){
        soundMap[1] = soundPool.load(context, R.raw.da2, 1)
    }

    fun playCtrlRing(){
        if(Config.ctrlRing) {
            soundPool.play(soundMap[1]!!, 1f, 1f, 1, 0, 1f)
        }
    }
}