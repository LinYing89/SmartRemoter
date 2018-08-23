package com.bairock.iot.smartremoter.settings

import android.content.Context
import android.preference.PreferenceManager

object Config {

    private const val keyServerName = "serverName"
    private const val keyRoutePsd = "routePsd"
    const val keyTouchRing = "switchTouchRing"
    private const val keyNeedLogin = "needLogin"

    var serverName = "051801.cn"
    var routePsd = ""
    var ctrlRing = true
    var needLogin = true

    fun init(context: Context){
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        serverName = shared.getString(keyServerName, serverName)
        routePsd = shared.getString(keyRoutePsd, routePsd)
        needLogin = shared.getBoolean(keyNeedLogin, true)
        ctrlRing = shared.getBoolean(keyTouchRing, true)
    }

    fun setRoutePsd(context: Context, routePsd: String){
        this.routePsd = routePsd
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = shared.edit()
        editor.putString(keyRoutePsd, routePsd)
        editor.apply()
    }
}