package com.bairock.iot.smartremoter.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bairock.iot.smartremoter.R

class DevicesFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "设备"
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_devices, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DevicesFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
