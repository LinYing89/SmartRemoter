package com.bairock.iot.smartremoter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.bairock.hamadev.database.AlarmTriggerDao
import com.bairock.iot.intelDev.device.Coordinator
import com.bairock.iot.intelDev.device.DevHaveChild
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.alarm.DevAlarm
import com.bairock.iot.intelDev.device.devcollect.DevCollect
import com.bairock.iot.intelDev.device.remoter.Remoter
import java.util.ArrayList

class DeviceDao(context: Context) {

    private var mContext = context
    private val mDatabase: SQLiteDatabase = SdDbHelper(context).writableDatabase

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dao: DeviceDao? = null

        fun get(context: Context): DeviceDao {
            if (null == dao) {
                dao = DeviceDao(context)
            }
            return dao!!
        }
    }

    private fun getContentValues(device: Device): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabDevice.Cols.ID, device.id)
        values.put(DbSb.TabDevice.Cols.DEVICE_TYPE, device.javaClass.simpleName)
        values.put(DbSb.TabDevice.Cols.ALIAS, device.alias)
        values.put(DbSb.TabDevice.Cols.CTRL_MODEL, device.findSuperParent().ctrlModel.toString())
        values.put(DbSb.TabDevice.Cols.VISIBILITY, device.isVisibility)
        values.put(DbSb.TabDevice.Cols.DELETED, device.isDeleted)
        values.put(DbSb.TabDevice.Cols.DEV_CATEGORY, device.devCategory.toString())
        values.put(DbSb.TabDevice.Cols.DEV_STATE_ID, device.devStateId)
        values.put(DbSb.TabDevice.Cols.GEAR, device.gear.toString())
        values.put(DbSb.TabDevice.Cols.MAIN_CODE_ID, device.mainCodeId)
        values.put(DbSb.TabDevice.Cols.NAME, device.name)
        values.put(DbSb.TabDevice.Cols.PLACE, device.place)
        values.put(DbSb.TabDevice.Cols.SORT_INDEX, device.sortIndex)
        values.put(DbSb.TabDevice.Cols.SUB_CODE, device.subCode)
        if (device is Coordinator) {
            values.put(DbSb.TabDevice.Cols.PANID, device.panid)
        }
        values.put(DbSb.TabDevice.Cols.DEV_GROUP_ID, device.findSuperParent().devGroup.id)
        if (device.parent != null) {
            values.put(DbSb.TabDevice.Cols.PARENT_ID, device.parent.id)
        }
        return values
    }

    private fun getUpdateIdContentValues(newId: String): ContentValues {
        val values = ContentValues()
        values.put(DbSb.TabDevice.Cols.ID, newId)
        return values
    }

    fun add(device: Device) {
        val list = find(DbSb.TabDevice.Cols.MAIN_CODE_ID + " = ? and " + DbSb.TabDevice.Cols.SUB_CODE + " = ?", arrayOf(device.mainCodeId, device.subCode))
        if (list.size > 0) {
            val devDb = list[0]
            device.id = devDb.id
            device.name = devDb.name
            device.isDeleted = false
            device.alias = devDb.alias
            device.ctrlModel = devDb.ctrlModel
            device.devCategory = devDb.devCategory
            device.gear = devDb.gear
            device.place = devDb.place
            device.sn = devDb.sn
            device.sortIndex = devDb.sortIndex
            update(device)
            if (device is DevHaveChild) {
                device.listDev = findChildDevice(device)
            }
            return
        }
        addDevice(device)
        //        ContentValues values = getContentValues(device);
        //        mDatabase.insert(DbSb.TabDevice.NAME, null, values);
        //        if(device instanceof DevHaveChild){
        //            for(Device dev : ((DevHaveChild)device).getListDev()){
        //                ContentValues values1 = getContentValues(dev);
        //                mDatabase.insert(DbSb.TabDevice.NAME, null, values1);
        //            }
        //        }
        //        if(device instanceof DevCollect){
        //            CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(mContext);
        //            collectPropertyDao.add(((DevCollect)device).getCollectProperty());
        //        }
    }

    private fun addDevice(device: Device) {
        val values1 = getContentValues(device)
        mDatabase.insert(DbSb.TabDevice.NAME, null, values1)

        val deviceLinkageDao = DeviceLinkageDao.Companion.get(mContext)
        for (deviceLinkage in device.listDeviceLinkage) {
            deviceLinkageDao.add(deviceLinkage)
        }

        if (device is DevHaveChild) {
            for (dev in device.listDev) {
                addDevice(dev)
            }
        } else if (device is DevCollect) {
            val collectPropertyDao = CollectPropertyDao.get(mContext)
            collectPropertyDao.add(device.collectProperty)
        } else if (device is Remoter) {
            val remoterKeyDao = RemoterKeyDao.Companion.get(mContext)
            for (remoterKey in device.listRemoterKey) {
                remoterKeyDao.add(remoterKey)
            }
        } else if (device is DevAlarm) {
            val alarmTriggerDao = AlarmTriggerDao.get(mContext)
            alarmTriggerDao.add(device.trigger)
        }
    }

    fun delete(device: Device) {
        if (device is DevHaveChild) {
            for (dev in device.listDev) {
                delete(dev)
            }
        } else if (device is DevCollect) {
            CollectPropertyDao.get(mContext).delete(device.collectProperty)
        } else if (device is Remoter) {
            val remoterKeyDao = RemoterKeyDao.Companion.get(mContext)
            for (remoterKey in device.listRemoterKey) {
                remoterKeyDao.delete(remoterKey)
            }
        } else if (device is DevAlarm) {
            val alarmTriggerDao = AlarmTriggerDao.get(mContext)
            alarmTriggerDao.delete(device.trigger)
        }
        mDatabase.delete(DbSb.TabDevice.NAME, DbSb.TabDevice.Cols.ID + "=?", arrayOf(device.id))
    }

    fun clean() {
        mDatabase.execSQL("delete from " + DbSb.TabDevice.NAME)
    }

    fun update(device: Device) {
        val values = getContentValues(device)
        mDatabase.update(DbSb.TabDevice.NAME, values,
                "id = ?",
                arrayOf(device.id))
    }

    fun updateId(oldId: String, newId: String) {
        val values = getUpdateIdContentValues(newId)
        mDatabase.update(DbSb.TabDevice.NAME, values,
                DbSb.TabDevice.Cols.ID + " = ?",
                arrayOf(oldId))
    }

    fun find(): List<Device> {
        //        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is ? and " + DbSb.TabDevice.Cols.DELETED + " = ?",new String[]{"null", "0"});
        val cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is null and deleted = 0", null)
        return createDevices(cursor)
    }

    fun findIncludeDeleted(): List<Device> {
        //        DeviceCursorWrapper cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is ? and " + DbSb.TabDevice.Cols.DELETED + " = ?",new String[]{"null", "0"});
        val cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " is null", null)
        return createDevices(cursor)
    }

    fun find(whereClause: String, whereArgs: Array<String>): List<Device> {
        val cursor = query(whereClause, whereArgs)
        return createDevices(cursor)
    }

    private fun findChildDevice(parent: Device): List<Device> {
        val cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " = ? and deleted = 0", arrayOf(parent.id))
        return createDevices(cursor)
    }

    private fun findChildDeviceIncludeDeleted(parent: Device): List<Device> {
        val cursor = query(DbSb.TabDevice.Cols.PARENT_ID + " = ?", arrayOf(parent.id))
        return createDevices(cursor)
    }

    private fun createDevices(cursor: DeviceCursorWrapper): List<Device> {
        val listDevice = ArrayList<Device>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val device = cursor.getDevice()
                initDevice(device)
                listDevice.add(device)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return listDevice
    }

    private fun initDevice(device: Device) {
        if (device is DevHaveChild) {
            val listChildDevice = findChildDevice(device)
            device.listDev.clear()
            for (device1 in listChildDevice) {
                initDevice(device1)
                //initDevCollect(device1);
                device.addChildDev(device1)
            }
        }
        initDevCollect(device)
        initRemoter(device)
        initDevAlarm(device)
    }

    private fun initDevCollect(device: Device) {
        if (device is DevCollect) {
            val collectPropertyDao = CollectPropertyDao.get(mContext)
            device.collectProperty = collectPropertyDao.find(device)
        }
    }

    private fun initRemoter(device: Device) {
        if (device is Remoter) {
            val remoterKeyDao = RemoterKeyDao.Companion.get(mContext)
            val listKey = remoterKeyDao.find(device)
            for (remoterKey in listKey) {
                device.addRemoterKey(remoterKey)
            }
        }
    }

    private fun initDevAlarm(device: Device) {
        if (device is DevAlarm) {
            val alarmTriggerDao = AlarmTriggerDao.get(mContext)
            device.trigger = alarmTriggerDao.find(device)
        }
    }

    private fun query(whereClause: String, whereArgs: Array<String>?): DeviceCursorWrapper {
        val cursor = mDatabase.query(
                DbSb.TabDevice.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        )// groupBy
        return DeviceCursorWrapper(cursor)
    }
}