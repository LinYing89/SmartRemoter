package com.bairock.iot.smartremoter

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.bairock.iot.smartremoter.adapter.MainPagerAdapter
import com.bairock.iot.smartremoter.main.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , BaseFragment.OnFragmentInteractionListener {

    private var whichFragment = 0

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //message.setText(R.string.title_home)
                viewPager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //message.setText(R.string.title_dashboard)
                viewPager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //message.setText(R.string.title_notifications)
                viewPager.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        navigation.selectedItemId = R.id.navigation_dashboard
        onTitle(1 )
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onTitle(fragment: Int) {
        whichFragment = fragment
        supportActionBar?.title = title
        when(fragment){
            0 ->{
                supportActionBar?.title = "设备"
            }
            1 ->{
                supportActionBar?.title = "控制"
            }
            2 ->{
                supportActionBar?.title = "我"
            }
        }
    }
}
