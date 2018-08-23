package com.bairock.iot.smartremoter.app

import com.bairock.iot.smartremoter.R

object Constant {
    /**
     * screen width
     */
    var displayWidth: Int = 0
    /**
     * screen height
     */
    var displayHeight: Int = 0

    /**
     * title height
     */
    var titleHeight: Int = 0

    fun displayWidthPx() : Int{
        return dip2px(displayWidth.toFloat())
    }

    fun displayHeightPx() : Int{
        return dip2px(displayHeight.toFloat())
    }

    fun getRemoterKeyWidth(): Int{
//        val pxValue2 = HamaApp.HAMA_CONTEXT.resources.getDimension(R.dimen.dp_60);//获取对应资源文件下的dp值
//        val dpValue = dip2px(pxValue2);//将px值转换成dp值
//        return dpValue
        return dip2px(60f)
    }

    fun dip2px(dpValue: Float): Int {
        val scale = HamaApp.HAMA_CONTEXT.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(pxValue: Float): Int {
        val scale = HamaApp.HAMA_CONTEXT.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}