package com.bairock.iot.smartremoter

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.bairock.iot.smartremoter.adapter.MainPagerAdapter
import com.bairock.iot.smartremoter.main.BaseFragment
import com.bairock.iot.smartremoter.main.IFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , BaseFragment.OnFragmentInteractionListener {

    private var fragment : IFragment? = null

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
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(null == fragment || !fragment!!.onKeyDown()){
                moveTaskToBack(true)
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onFragmentInteraction(fragment: IFragment?) {
        this.fragment = fragment
    }
}
