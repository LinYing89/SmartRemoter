package com.bairock.iot.smartremoter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bairock.iot.smartremoter.R

class AdapterSelectRemoter(val context: Context) : BaseAdapter() {

    private val remoters = context.resources.getStringArray(R.array.array_remoter)!!
    private val remoterImgs = arrayOf(R.drawable.ic_tv,  R.drawable.ic_curtain, R.drawable.ic_custom_remoter)

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mInflater.inflate(R.layout.layout_select_remoter, null)
        val tvTime = view.findViewById(R.id.txtName) as TextView
        val imgDevice = view.findViewById(R.id.imgDevice) as ImageView

        tvTime.text = remoters[p0]
        imgDevice.setImageResource(remoterImgs[p0])
        return view
    }

    override fun getItem(p0: Int): Any {
        return remoters[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return remoters.size
    }
}