package com.bairock.iot.smartremoter.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.bairock.iot.smartremoter.main.CtrlFragment
import com.bairock.iot.smartremoter.main.DevicesFragment
import com.bairock.iot.smartremoter.main.MeFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DevicesFragment.newInstance()
            1 -> fragment = CtrlFragment.newInstance()
            2 -> fragment = MeFragment.newInstance()
        }
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "设备"
            1 -> return "控制"
            2 -> return "我"
        }
        return null
    }
}