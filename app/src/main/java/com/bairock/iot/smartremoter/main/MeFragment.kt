package com.bairock.iot.smartremoter.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bairock.iot.smartremoter.R
import kotlinx.android.synthetic.main.fragment_me.*
import com.bairock.iot.smartremoter.app.LogUtils
import com.bairock.iot.smartremoter.logs.BridgesStateActivity
import com.bairock.iot.smartremoter.settings.SettingsActivity


class MeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (LogUtils.APP_DBG) {
            layoutTest.visibility = View.VISIBLE
        } else {
            layoutTest.visibility = View.GONE
        }

        layoutTest.setOnClickListener{
            startActivity(Intent(this.context, BridgesStateActivity::class.java))
        }

        layoutSettings.setOnClickListener{
            startActivity(Intent(this.context, SettingsActivity::class.java))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                MeFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
