package com.bairock.iot.smartremoter.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bairock.iot.smartremoter.R

class MeFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "我"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
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
