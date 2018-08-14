package com.bairock.iot.smartremoter.data

import android.database.Cursor
import android.database.CursorWrapper
import com.bairock.iot.intelDev.linkage.Effect
import com.bairock.iot.intelDev.user.DevGroup

class EffectWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    companion object {
        var devGroup: DevGroup? = null
    }

    fun getEffect(): Effect? {
        val devId = getString(getColumnIndex(DbSb.TabEffect.Cols.DEV_ID))
        val device = devGroup!!.findDeviceByDevId(devId) ?: return null

        val id = getString(getColumnIndex(DbSb.TabEffect.Cols.ID))
        //String linkageId = getString(getColumnIndex(DbSb.TabEffect.Cols.LINKAGE_ID));
        val dsId = getString(getColumnIndex(DbSb.TabEffect.Cols.DS_ID))
        val deleted = getString(getColumnIndex(DbSb.TabEffect.Cols.DELETED)) == "1"

        val effectContent = getString(getColumnIndex(DbSb.TabEffect.Cols.EFFECT_CONTENT))
        val strEffectCount = getString(getColumnIndex(DbSb.TabEffect.Cols.EFFECT_COUNT))
        var effectCount = 0
        try {
            effectCount = Integer.parseInt(strEffectCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val effect = Effect()
        effect.id = id
        effect.dsId = dsId
        effect.isDeleted = deleted
        effect.device = device
        effect.effectContent = effectContent
        effect.effectCount = effectCount
        return effect
    }
}