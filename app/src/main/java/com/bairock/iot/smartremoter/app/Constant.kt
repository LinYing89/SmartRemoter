package com.bairock.iot.smartremoter.app

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
//        return com.videogo.util.Utils.px2dip(HamaApp.HAMA_CONTEXT, 50f)
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