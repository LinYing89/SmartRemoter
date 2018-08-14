package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.ChainHolder
import com.bairock.iot.intelDev.linkage.LinkageHolder
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder
import com.bairock.iot.intelDev.linkage.loop.LoopHolder
import com.bairock.iot.intelDev.linkage.timing.TimingHolder

class LinkageHolderWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getLinkageHolder(): LinkageHolder {
        val id = getString(getColumnIndex(DbSb.TabLinkageHolder.Cols.ID))
        val type = getString(getColumnIndex(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE))
        val enable = getString(getColumnIndex(DbSb.TabLinkageHolder.Cols.ENABLE)) == "1"
        var linkageHolder: LinkageHolder? = null
        if (type == "ChainHolder") {
            linkageHolder = ChainHolder()
        } else if (type == "LoopHolder") {
            linkageHolder = LoopHolder()
        } else if (type == "TimingHolder") {
            linkageHolder = TimingHolder()
        } else if (type == "GuaguaHolder") {
            linkageHolder = GuaguaHolder()
        }
        linkageHolder!!.id = id
        linkageHolder.isEnable = enable
        return linkageHolder
    }
}