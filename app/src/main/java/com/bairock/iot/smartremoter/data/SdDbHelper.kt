package com.bairock.iot.smartremoter.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bairock.hamadev.database.AlarmTriggerDao
import com.bairock.iot.intelDev.linkage.SubChain
import com.bairock.iot.intelDev.linkage.loop.ZLoop
import com.bairock.iot.intelDev.linkage.timing.Timing
import com.bairock.iot.intelDev.user.DevGroup
import com.bairock.iot.intelDev.user.User
import com.bairock.iot.smartremoter.app.HamaApp
import com.bairock.iot.smartremoter.data.DbSb.*

class SdDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        private const val VERSION = 1
        private const val DATABASE_NAME = "sd_db.db"

        fun getDbUser(): User {
            val userDao = UserDao.get(HamaApp.HAMA_CONTEXT)
            var user = userDao.getUser()

            if(user == null){
                user = User()
                user.addGroup(DevGroup())
                return user
            }
            val devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT)
            val group = devGroupDao.find()
            if(group == null){
                user.addGroup(DevGroup())
                return user
            }

            user.addGroup(group)

            val deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT)
            val listDevice = deviceDao.findIncludeDeleted()

            val deviceLinkageDao = DeviceLinkageDao.get(HamaApp.HAMA_CONTEXT)
            for (device in listDevice) {
                val listDeviceLinkage = deviceLinkageDao.find(device, listDevice)
                device.listDeviceLinkage = listDeviceLinkage
                group.addDevice(device)
            }

            LinkageConditionWrapper.devGroup = group
            EffectWrapper.devGroup = group
            //连锁
            val linkageHolderDao = LinkageHolderDao.get(HamaApp.HAMA_CONTEXT)
            group.listLinkageHolder = linkageHolderDao.findByDevGroupId(group.id)

            val linkageDao = LinkageDao.get(HamaApp.HAMA_CONTEXT)
            for (linkageHolder in group.listLinkageHolder) {
                val listLinkage = linkageDao.findChainByLinkageHolderId(linkageHolder.id)
                linkageHolder.listLinkage = listLinkage
            }
            return user
        }

        fun replaceDbUser(user: User) {
            cleanDb()
            val devGroup = user.listDevGroup[0]
            UserDao.get(HamaApp.HAMA_CONTEXT).addUser(user)
            DevGroupDao.get(HamaApp.HAMA_CONTEXT).add(devGroup)
            val deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT)
            for (device in devGroup.listDevice) {
                deviceDao.add(device)
            }

            val linkageHolderDao = LinkageHolderDao.get(HamaApp.HAMA_CONTEXT)
            for (linkageHolder in devGroup.listLinkageHolder) {
                linkageHolderDao.add(linkageHolder)
            }

            val linkageDao = LinkageDao.get(HamaApp.HAMA_CONTEXT)
            val linkageConditionDao = LinkageConditionDao.get(HamaApp.HAMA_CONTEXT)
            val effectDao = EffectDao.get(HamaApp.HAMA_CONTEXT)

            for (linkageHolder in devGroup.listLinkageHolder) {
                for (linkage in linkageHolder.listLinkage) {
                    linkageDao.add(linkage, linkageHolder.id)
                    for (effect in linkage.listEffect) {
                        effectDao.add(effect, linkage.id)
                    }
                    if (linkage is SubChain) {
                        for (linkageCondition in linkage.listCondition) {
                            linkageConditionDao.add(linkageCondition, linkage.id)
                        }
                        if (linkage is ZLoop) {
                            val loopDurationDao = LoopDurationDao.get(HamaApp.HAMA_CONTEXT)
                            val myTimeDao = MyTimeDao.get(HamaApp.HAMA_CONTEXT)
                            for (loopDuration in linkage.listLoopDuration) {
                                loopDurationDao.add(loopDuration, linkage.id)
                                myTimeDao.add(loopDuration.onKeepTime, loopDuration.id)
                                myTimeDao.add(loopDuration.offKeepTime, loopDuration.id)
                            }
                        }
                    } else if (linkage is Timing) {
                        val zTimerDao = ZTimerDao.get(HamaApp.HAMA_CONTEXT)
                        val myTimeDao = MyTimeDao.get(HamaApp.HAMA_CONTEXT)
                        val weekHelperDao = WeekHelperDao.get(HamaApp.HAMA_CONTEXT)
                        for (zTimer in linkage.listZTimer) {
                            zTimerDao.add(zTimer, zTimer.id)
                            myTimeDao.add(zTimer.onTime, zTimer.id)
                            myTimeDao.add(zTimer.offTime, zTimer.id)
                            weekHelperDao.add(zTimer.weekHelper)
                        }
                    }
                }
            }
        }

        private fun cleanDb() {
            CollectPropertyDao.get(HamaApp.HAMA_CONTEXT).clean()
            ValueTriggerDao.get(HamaApp.HAMA_CONTEXT).clean()
            AlarmTriggerDao.get(HamaApp.HAMA_CONTEXT).clean()
            RemoterKeyDao.get(HamaApp.HAMA_CONTEXT).clean()
            DevGroupDao.get(HamaApp.HAMA_CONTEXT).clean()
            DeviceDao.get(HamaApp.HAMA_CONTEXT).clean()
            EffectDao.get(HamaApp.HAMA_CONTEXT).clean()
            LinkageConditionDao.get(HamaApp.HAMA_CONTEXT).clean()
            LinkageDao.get(HamaApp.HAMA_CONTEXT).clean()
            LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).clean()
            LoopDurationDao.get(HamaApp.HAMA_CONTEXT).clean()
            MyTimeDao.get(HamaApp.HAMA_CONTEXT).clean()
            UserDao.get(HamaApp.HAMA_CONTEXT).clean()
            WeekHelperDao.get(HamaApp.HAMA_CONTEXT).clean()
            ZTimerDao.get(HamaApp.HAMA_CONTEXT).clean()
            DeviceLinkageDao.get(HamaApp.HAMA_CONTEXT).clean()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        //创建user表
        db.execSQL("create table " + TabUser.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TabUser.Cols.EMAIL + ", " +
                TabUser.Cols.NAME + ", " +
                TabUser.Cols.PET_NAME + ", " +
                TabUser.Cols.PSD + ", " +
                TabUser.Cols.REGISTER_TIME + ", " +
                TabUser.Cols.TEL +
                ")"
        )
        //创建devGroup表
        db.execSQL("create table " + TabDevGroup.NAME + "(" +
                TabDevGroup.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDevGroup.Cols.NAME + ", " +
                TabDevGroup.Cols.PET_NAME + ", " +
                TabDevGroup.Cols.PSD + ", " +
                TabDevGroup.Cols.USER_ID +
                ")"
        )
        //创建device表
        db.execSQL("create table " + TabDevice.NAME + "(" +
                TabDevice.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDevice.Cols.DEVICE_TYPE + ", " +
                TabDevice.Cols.ALIAS + ", " +
                TabDevice.Cols.CTRL_MODEL + ", " +
                TabDevice.Cols.VISIBILITY + ", " +
                TabDevice.Cols.DELETED + ", " +
                TabDevice.Cols.DEV_CATEGORY + ", " +
                TabDevice.Cols.DEV_STATE_ID + ", " +
                TabDevice.Cols.GEAR + ", " +
                TabDevice.Cols.MAIN_CODE_ID + ", " +
                TabDevice.Cols.NAME + ", " +
                TabDevice.Cols.PLACE + ", " +
                TabDevice.Cols.SN + ", " +
                TabDevice.Cols.SORT_INDEX + ", " +
                TabDevice.Cols.SUB_CODE + ", " +
                TabDevice.Cols.PANID + ", " +
                TabDevice.Cols.DEV_GROUP_ID + ", " +
                TabDevice.Cols.PARENT_ID +
                ")"
        )
        //创建collect property表
        db.execSQL("create table " + TabCollectProperty.NAME + "(" +
                TabCollectProperty.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabCollectProperty.Cols.CREST_VALUE + ", " +
                TabCollectProperty.Cols.CREST_REFER_VALUE + ", " +
                TabCollectProperty.Cols.CURRENT_VALUE + ", " +
                TabCollectProperty.Cols.LEAST_VALUE + ", " +
                TabCollectProperty.Cols.LEAST_REFER_VALUE + ", " +
                TabCollectProperty.Cols.PERCENT + ", " +
                TabCollectProperty.Cols.SIGNAL_SRC + ", " +
                TabCollectProperty.Cols.UNIT_SYMBOL + ", " +
                TabCollectProperty.Cols.CALIBRATION_VALUE + ", " +
                TabCollectProperty.Cols.FORMULA + ", " +
                TabCollectProperty.Cols.DEV_COLLECT_ID +
                ")"
        )

        //创建value trigger表
        db.execSQL("create table " + TabValueTrigger.NAME + "(" +
                TabValueTrigger.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabValueTrigger.Cols.NAME + ", " +
                TabValueTrigger.Cols.ENABLE + ", " +
                TabValueTrigger.Cols.TRIGGER_VALUE + ", " +
                TabValueTrigger.Cols.COMPARE_SYMBOL + ", " +
                TabValueTrigger.Cols.MESSAGE + ", " +
                TabValueTrigger.Cols.COLLECT_PROPERTY_ID +
                ")"
        )

        //创建alarm trigger表
        db.execSQL("create table " + TabAlarmTrigger.NAME + "(" +
                TabAlarmTrigger.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabAlarmTrigger.Cols.ENABLE + ", " +
                TabAlarmTrigger.Cols.MESSAGE + ", " +
                TabAlarmTrigger.Cols.DEV_ALARM_ID +
                ")"
        )

        //创建remote key表
        db.execSQL("create table " + TabRemoterKey.NAME + "(" +
                TabRemoterKey.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabRemoterKey.Cols.REMOTE_ID + ", " +
                TabRemoterKey.Cols.NAME + ", " +
                TabRemoterKey.Cols.NUMBER + ", " +
                TabRemoterKey.Cols.LOCATION_X + ", " +
                TabRemoterKey.Cols.LOCATION_Y +
                ")"
        )

        //创建deviceLinkage表
        db.execSQL("create table " + TabDeviceLinkage.NAME + "(" +
                TabDeviceLinkage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDeviceLinkage.Cols.SWITCH_MODEL + ", " +
                TabDeviceLinkage.Cols.VALUE1 + ", " +
                TabDeviceLinkage.Cols.VALUE2 + ", " +
                TabDeviceLinkage.Cols.SOURCE_DEVICE_ID + ", " +
                TabDeviceLinkage.Cols.TARGET_DEV_ID +
                ")"
        )

        //创建linkage holder表
        db.execSQL("create table " + TabLinkageHolder.NAME + "(" +
                TabLinkageHolder.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkageHolder.Cols.DEVGROUP_ID + ", " +
                TabLinkageHolder.Cols.LINKAGE_TYPE + ", " +
                TabLinkageHolder.Cols.ENABLE +
                ")"
        )
        //创建linkage 子连锁数据表
        db.execSQL("create table " + TabLinkage.NAME + "(" +
                TabLinkage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkage.Cols.LINKAGE_TYPE + ", " +
                TabLinkage.Cols.DELETED + ", " +
                TabLinkage.Cols.ENABLE + ", " +
                TabLinkage.Cols.NAME + ", " +
                TabLinkage.Cols.TRIGGERED + ", " +
                TabLinkage.Cols.LOOP_COUNT + ", " +
                TabLinkage.Cols.LINKAGE_HOLDER_ID +
                ")"
        )
        //创建linkage condition 连锁条件数据表
        db.execSQL("create table " + TabLinkageCondition.NAME + "(" +
                TabLinkageCondition.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkageCondition.Cols.COMPARE_SYMBOL + ", " +
                TabLinkageCondition.Cols.COMPARE_VALUE + ", " +
                TabLinkageCondition.Cols.DELETED + ", " +
                TabLinkageCondition.Cols.LOGIC + ", " +
                TabLinkageCondition.Cols.TRIGGER_STYLE + ", " +
                TabLinkageCondition.Cols.DEV_ID + ", " +
                TabLinkageCondition.Cols.SUBCHAIN_ID +
                ")"
        )
        //创建effect 连锁影响数据表
        db.execSQL("create table " + TabEffect.NAME + "(" +
                TabEffect.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabEffect.Cols.DELETED + ", " +
                TabEffect.Cols.DS_ID + ", " +
                TabEffect.Cols.EFFECT_CONTENT + ", " +
                TabEffect.Cols.EFFECT_COUNT + ", " +
                TabEffect.Cols.DEV_ID + ", " +
                TabEffect.Cols.LINKAGE_ID +
                ")"
        )
        //创建my time 时分秒数据表
        db.execSQL("create table " + TabMyTime.NAME + "(" +
                TabMyTime.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabMyTime.Cols.HOUR + ", " +
                TabMyTime.Cols.MINUTE + ", " +
                TabMyTime.Cols.TYPE + ", " +
                TabMyTime.Cols.TIMER_ID + ", " +
                TabMyTime.Cols.SECOND +
                ")"
        )
        //创建week helper 星期助手数据表
        db.execSQL("create table " + TabWeekHelper.NAME + "(" +
                TabWeekHelper.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabWeekHelper.Cols.ZTIMER_ID + ", " +
                TabWeekHelper.Cols.SUN + ", " +
                TabWeekHelper.Cols.MON + ", " +
                TabWeekHelper.Cols.TUES + ", " +
                TabWeekHelper.Cols.WED + ", " +
                TabWeekHelper.Cols.THUR + ", " +
                TabWeekHelper.Cols.FRI + ", " +
                TabWeekHelper.Cols.SAT +
                ")"
        )
        //创建ztimer 子定时数据表
        db.execSQL("create table " + TabZTimer.NAME + "(" +
                TabZTimer.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabZTimer.Cols.DELETED + ", " +
                TabZTimer.Cols.ENABLE + ", " +
                TabZTimer.Cols.TIMING_ID +
                ")"
        )
        //创建loop duration 循环区间，开区间，关区间数据表
        db.execSQL("create table " + TabLoopDuration.NAME + "(" +
                TabLoopDuration.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLoopDuration.Cols.DELETED + ", " +
                TabLoopDuration.Cols.ZLOOP_ID +
                ")"
        )

        //创建alarm message数据表
        db.execSQL("create table " + TabAlarmMessage.NAME + "(" +
                TabAlarmMessage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabAlarmMessage.Cols.NAME + ", " +
                TabAlarmMessage.Cols.MESSAGE + ", " +
                TabAlarmMessage.Cols.TIME +
                ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

}