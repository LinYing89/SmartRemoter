package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.Linkage
import com.bairock.iot.intelDev.linkage.SubChain
import com.bairock.iot.intelDev.linkage.loop.ZLoop
import com.bairock.iot.intelDev.linkage.timing.Timing

class LinkageWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getLinkage(): Linkage {
        val linkageType = getString(getColumnIndex(DbSb.TabLinkage.Cols.LINKAGE_TYPE))
        //String linkageHolderId = getString(getColumnIndex(DbSb.TabLinkageDevValue.Cols.LINKAGE_HOLDER_ID));
        val id = getString(getColumnIndex(DbSb.TabLinkage.Cols.ID))
        val name = getString(getColumnIndex(DbSb.TabLinkage.Cols.NAME))
        val enable = getString(getColumnIndex(DbSb.TabLinkage.Cols.ENABLE)) == "1"

        val strLoopCount = getString(getColumnIndex(DbSb.TabLinkage.Cols.LOOP_COUNT))
        var iLoopCount = 0
        try {
            iLoopCount = Integer.parseInt(strLoopCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var linkage: Linkage? = null
        when (linkageType) {
            "SubChain" -> linkage = SubChain()
            "Timing" -> linkage = Timing()
            "ZLoop" -> {
                linkage = ZLoop()
                linkage.loopCount = iLoopCount
            }
        }

        linkage!!.id = id
        linkage.name = name
        linkage.isEnable = enable
        return linkage
    }
}