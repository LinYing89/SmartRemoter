package com.bairock.iot.smartremoter.remoter

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.*
import com.bairock.iot.intelDev.device.remoter.RemoterKey
import com.bairock.iot.smartremoter.R
import com.bairock.iot.smartremoter.app.HamaApp
import kotlinx.android.synthetic.main.activity_study_key.*
import java.lang.ref.WeakReference

class StudyKeyActivity : AppCompatActivity() {

    companion object {
        var remoterKey : RemoterKey? = null
        var STUDY_READY = 0
        var STUDIED = 1
        var TESTED = 2

        var handler : Handler? = null
        private var waitTask : WaitTask? = null
    }

    private var studyProgress : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_key)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "按键:${remoterKey!!.name}"
        }else{
            title = "按键:${remoterKey!!.name}"
        }

        setListener()

        handler = MyHandler(this)

        studyProgress = 0
        refreshUi(studyProgress)
    }

    override fun onDestroy() {
        super.onDestroy()
        remoterKey = null
        handler!!.removeCallbacksAndMessages(null)
        handler = null
        if(null != waitTask && waitTask!!.status == AsyncTask.Status.RUNNING){
            waitTask!!.cancel(true)
            waitTask = null
        }
    }

    private fun setListener(){
        btnStudy.setOnClickListener(onClickListener)
        btnTest.setOnClickListener(onClickListener)
        btnTestSuccess.setOnClickListener(onClickListener)
        btnReStudy.setOnClickListener(onClickListener)
    }

    private fun refreshUi(progress : Int){
        if(null != waitTask && waitTask!!.status == AsyncTask.Status.RUNNING){
            waitTask!!.cancel(true)
        }
        when(progress){
            STUDY_READY ->{
                txtInfo.text = "请点击开始学习按钮"
                llBegin.visibility = View.VISIBLE
                btnStudy.visibility = View.VISIBLE
                btnTest.visibility = View.GONE
                llTest.visibility = View.GONE
            }
            STUDIED ->{
                txtInfo.text = "请点击测试按钮,测试是否可以控制"
                llBegin.visibility = View.VISIBLE
                //pbWait.visibility = View.GONE
                btnStudy.visibility = View.GONE
                btnTest.visibility = View.VISIBLE
                llTest.visibility = View.GONE
            }
            TESTED ->{
                txtInfo.text = "如果测试成功请点击测试成功按钮,如果测试失败请点击重新学习按钮"
                llBegin.visibility = View.GONE
                btnStudy.visibility = View.GONE
                btnTest.visibility = View.GONE
                llTest.visibility = View.VISIBLE
            }
            else ->{
                Toast.makeText(this, "学习成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val onClickListener : View.OnClickListener = View.OnClickListener {
        when(it.id){
            R.id.btnStudy ->{
                HamaApp.sendOrder(remoterKey!!.remoter.parent, remoterKey!!.createStudyKeyOrder(), true)
                btnStudy.visibility = View.GONE
                txtInfo.text = "请将实体遥控器对准智能遥控器,并按一下想要学习的按键"
                showWait()
            }
            R.id.btnTest ->{
                HamaApp.sendOrder(remoterKey!!.remoter.parent, remoterKey!!.createTestKeyOrder(), true)
                btnTest.visibility = View.GONE
                showWait()
            }
            R.id.btnTestSuccess ->{
                HamaApp.sendOrder(remoterKey!!.remoter.parent, remoterKey!!.createSaveKeyOrder(), true)
                llTest.visibility = View.GONE
                showWait()
            }
            R.id.btnReStudy ->{
                HamaApp.sendOrder(remoterKey!!.remoter.parent, remoterKey!!.createTestKeyOrder(), true)
                studyProgress = 0
                refreshUi(studyProgress)
            }
        }
    }

    private fun showWait(){
        //pbWait.visibility = View.VISIBLE
        waitTask = WaitTask(this)
        waitTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        //waitTask!!.ex()
    }

    private class MyHandler(activity: StudyKeyActivity) : Handler(){
        private val weakActivity = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val  activity = weakActivity.get()!!
            activity.refreshUi(++activity.studyProgress)
        }
    }

    private class WaitTask(activity: StudyKeyActivity) : AsyncTask<Void, Void, Boolean>(){

        private val weakActivity = WeakReference(activity)

        override fun onPreExecute() {
            val myActivity = weakActivity.get()!!
            myActivity.pbWait.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            for(i in 0..8){
                if(isCancelled){
                    return false
                }
                try {
                    Thread.sleep(1000)
                }catch (ex:Exception){
                    return false
                }
            }
            return true
        }

        override fun onPostExecute(result: Boolean) {
            val myActivity = weakActivity.get()!!
            if(result){
                Toast.makeText(myActivity, "学习失败,智能遥控器无返回", Toast.LENGTH_SHORT).show()
                myActivity.refreshUi(myActivity.studyProgress)
                myActivity.pbWait.visibility = View.GONE
            }else{
                myActivity.pbWait.visibility = View.GONE
            }
        }

        override fun onCancelled() {
            val myActivity = weakActivity.get()!!
            myActivity.pbWait.visibility = View.GONE
        }
    }
}
