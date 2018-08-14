package com.bairock.iot.smartremoter.data

class DbSb {

    /**
     * user 数据表
     */
    object TabUser {
        /**
         * 数据表表名
         */
        val NAME = "user"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 邮箱
             */
            val ID = "_id"
            /**
             * 邮箱
             */
            val EMAIL = "email"
            /**
             * 用户名
             */
            val NAME = "name"
            /**
             * 用户昵称
             */
            val PET_NAME = "petName"
            /**
             * 用户密码
             */
            val PSD = "psd"
            /**
             * 注册时间
             */
            val REGISTER_TIME = "registerTime"
            /**
             * 电话
             */
            val TEL = "tel"
        }
    }

    /**
     * devGroup 数据表
     */
    object TabDevGroup {
        /**
         * 数据表表名
         */
        val NAME = "devgroup"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，UUID
             */
            val ID = "id"
            /**
             * 组名
             */
            const val NAME = "name"
            /**
             * 组昵称
             */
            const val PET_NAME = "petName"
            /**
             * 组密码
             */
            const val PSD = "psd"
            /**
             * 用户表外键
             */
            const val USER_ID = "user_id"
        }
    }

    /**
     * device 数据表
     */
    object TabDevice {
        /**
         * 数据表表名
         */
        val NAME = "device"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，UUID
             */
            val ID = "id"
            /**
             * 设备类型，开关、液位计
             */
            val DEVICE_TYPE = "device_type"
            /**
             * 别名
             */
            val ALIAS = "alias"
            /**
             * 控制模式，本地、远程
             */
            val CTRL_MODEL = "ctrlModel"
            /**
             * 是否可见
             */
            val VISIBILITY = "visibility"
            /**
             * 是否已删除
             */
            val DELETED = "deleted"
            /**
             * 设备分类
             */
            val DEV_CATEGORY = "devCategory"
            /**
             * 设备状态id
             */
            val DEV_STATE_ID = "devStateId"
            /**
             * 设备档位
             */
            val GEAR = "gear"
            /**
             * 主编吗id
             */
            val MAIN_CODE_ID = "mainCodeId"
            /**
             * 设备名称
             */
            val NAME = "name"
            /**
             * 设备位置
             */
            val PLACE = "place"
            /**
             * 设备序列号
             */
            val SN = "sn"
            /**
             * 排序索引
             */
            val SORT_INDEX = "sortIndex"
            /**
             * 设备子编码
             */
            val SUB_CODE = "subCode"
            /**
             * 协调器panid
             */
            val PANID = "panid"
            /**
             * 设备组外键
             */
            val DEV_GROUP_ID = "devGroup_id"
            /**
             * 父设备外键
             */
            val PARENT_ID = "parent_id"
        }
    }

    /**
     * collect property 采集设备属性数据表
     */
    object TabCollectProperty {
        /**
         * 数据表表名
         */
        val NAME = "collectproperty"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 最大值采集值
             */
            val CREST_VALUE = "crestValue"
            /**
             * 最大值采集值对应的最大使用值
             */
            val CREST_REFER_VALUE = "crestReferValue"
            /**
             * 当前值
             */
            val CURRENT_VALUE = "currentValue"
            /**
             * 最小值采集值
             */
            val LEAST_VALUE = "leastValue"
            /**
             * 最小值采集值对应的最小使用值
             */
            val LEAST_REFER_VALUE = "leastReferValue"
            /**
             * 百分比
             */
            val PERCENT = "percent"
            /**
             * 信号源
             */
            val SIGNAL_SRC = "signalSrc"
            /**
             * 单位符号
             */
            val UNIT_SYMBOL = "unitSymbol"
            /**
             * 标定值
             */
            val CALIBRATION_VALUE = "calibrationValue"
            /**
             * 公式
             */
            val FORMULA = "formula"
            /**
             * 采集设备外键
             */
            val DEV_COLLECT_ID = "devCollect_id"
        }
    }

    /**
     * value trigger 值触发器表
     */
    object TabValueTrigger {
        /**
         * 数据表表名
         */
        val NAME = "valueTrigger"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 名称
             */
            val NAME = "name"
            /**
             * 使能
             */
            val ENABLE = "enable"
            /**
             * 触发值
             */
            val TRIGGER_VALUE = "triggerValue"
            /**
             * 比较符号
             */
            val COMPARE_SYMBOL = "compareSymbol"
            /**
             * 触发信息
             */
            val MESSAGE = "message"
            /**
             * 采集设备外键
             */
            val COLLECT_PROPERTY_ID = "collectProperty_id"
        }
    }

    /**
     * alarm trigger 报警触发器表
     */
    object TabAlarmTrigger {
        /**
         * 数据表表名
         */
        val NAME = "alarmTrigger"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 使能
             */
            val ENABLE = "enable"
            /**
             * 触发信息
             */
            val MESSAGE = "message"
            /**
             * 报警设备外键
             */
            val DEV_ALARM_ID = "devAlarm_id"
        }
    }

    /**
     * collect property 采集设备属性数据表
     */
    object TabRemoterKey {
        /**
         * 数据表表名
         */
        val NAME = "remoterKey"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 遥控器id
             */
            val REMOTE_ID = "remote_id"
            /**
             * 按键名称
             */
            val NAME = "name"
            /**
             * 按键编号
             */
            val NUMBER = "number"
            /**
             * 按键X轴位置
             */
            val LOCATION_X = "locationX"
            /**
             * 按键Y轴位置
             */
            val LOCATION_Y = "locationY"
        }
    }

    /**
     * device linkage 设备连锁表
     */
    object TabDeviceLinkage {
        /**
         * 数据表表名
         */
        val NAME = "deviceLinkage"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 开关模式
             * 0表示等于值1开，等于值2关
             * 1表示小于值1开，大于值2关
             * 2 表示小于值1关，大于值2开
             */
            val SWITCH_MODEL = "switchModel"
            /**
             * 值1
             */
            val VALUE1 = "value1"
            /**
             * 值2
             */
            val VALUE2 = "value2"
            /**
             * 源设备
             */
            val SOURCE_DEVICE_ID = "sourceDevice_id"
            /**
             * 目标设备
             */
            val TARGET_DEV_ID = "targetDev_id"
        }
    }

    /**
     * linkage holder 根连锁数据表
     */
    object TabLinkageHolder {
        /**
         * 数据表表名
         */
        val NAME = "linkageholder"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 连锁类型，连锁、循环、定时、呱呱
             */
            val LINKAGE_TYPE = "linkage_type"
            /**
             * 是否使能
             */
            val ENABLE = "enable"
            /**
             * 组id
             */
            val DEVGROUP_ID = "devGroup_id"
        }
    }

    /**
     * linkage device value 普通设备值连锁数据表
     */
    object TabLinkage {
        /**
         * 数据表表名
         */
        val NAME = "linkage"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 子连锁类型，连锁、定时、循环
             */
            val LINKAGE_TYPE = "linkage_type"
            /**
             * 是否已删除
             */
            val DELETED = "deleted"
            /**
             * 是否使能
             */
            val ENABLE = "enable"
            /**
             * 连锁名称
             */
            val NAME = "name"
            /**
             * 是否触发了
             */
            val TRIGGERED = "triggered"
            /**
             * 循环次数，循环可用
             */
            val LOOP_COUNT = "loopCount"
            /**
             * 根连锁id，关联linkageHolder表的id
             */
            val LINKAGE_HOLDER_ID = "linkageHolder_id"
        }
    }

    /**
     * linkage condition 连锁条件数据表
     */
    object TabLinkageCondition {
        /**
         * 数据表表名
         */
        val NAME = "linkagecondition"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 比较符号，and、or
             */
            val COMPARE_SYMBOL = "compareSymbol"
            /**
             * 比较值
             */
            val COMPARE_VALUE = "compareValue"
            /**
             * 是否已删除
             */
            val DELETED = "deleted"
            /**
             * 比较逻辑
             */
            val LOGIC = "logic"
            /**
             * 触发值类型
             */
            val TRIGGER_STYLE = "triggerStyle"
            /**
             * 设备id，设备表外键
             */
            val DEV_ID = "dev_id"
            /**
             * 连锁id，子连锁表外键
             */
            val SUBCHAIN_ID = "subChain_id"
        }
    }

    /**
     * effect 连锁影响数据表
     */
    object TabEffect {
        /**
         * 数据表表名
         */
        val NAME = "effect"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 是否已删除
             */
            val DELETED = "deleted"
            /**
             * 影响设备到某状态的设备状态id
             */
            val DS_ID = "dsId"
            /**
             * 影响内容
             */
            val EFFECT_CONTENT = "effectContent"
            /**
             * 影响次数
             */
            val EFFECT_COUNT = "effectCount"
            /**
             * 设备id，设备表外键
             */
            val DEV_ID = "dev_id"
            /**
             * 连锁id，子连锁表外键
             */
            val LINKAGE_ID = "linkage_id"
        }
    }

    /**
     * my time 时分秒数据表
     */
    object TabMyTime {
        /**
         * 数据表表名
         */
        val NAME = "mytime"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 时
             */
            val HOUR = "hour"
            /**
             * 分
             */
            val MINUTE = "minute"
            /**
             * 秒
             */
            val SECOND = "second"
            /**
             * 类型0为关时间，1为开时间
             */
            val TYPE = "type"
            /**
             * 持有者id
             */
            val TIMER_ID = "timerId"
        }
    }

    /**
     * week helper 星期助手数据表
     */
    object TabWeekHelper {
        /**
         * 数据表表名
         */
        val NAME = "weekhelper"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 周日
             */
            val SUN = "sun"
            /**
             * 周一
             */
            val MON = "mon"
            /**
             * 周二
             */
            val TUES = "tues"
            /**
             * 周三
             */
            val WED = "wed"
            /**
             * 周四
             */
            val THUR = "thur"
            /**
             * 周五
             */
            val FRI = "fri"
            /**
             * 周六
             */
            val SAT = "sat"
            /**
             * 持有者id
             */
            val ZTIMER_ID = "zTimer_id"
        }
    }

    /**
     * ztimer 子定时条件数据表
     */
    object TabZTimer {
        /**
         * 数据表表名
         */
        val NAME = "ztimer"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 是否已删除
             */
            val DELETED = "deleted"
            /**
             * 是否使能
             */
            val ENABLE = "enable"
            /**
             * 定时id，定时表外键
             */
            val TIMING_ID = "timing_id"
        }
    }

    /**
     * loop duration 循环区间，开区间，关区间数据表
     */
    object TabLoopDuration {
        /**
         * 数据表表名
         */
        val NAME = "loopduration"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 是否已删除
             */
            val DELETED = "deleted"
            /**
             * 连锁id，连锁（循环）表外键
             */
            val ZLOOP_ID = "zLoop_id"
        }
    }

    /**
     * alarm message 报警信息数据表
     */
    object TabAlarmMessage {
        /**
         * 数据表表名
         */
        val NAME = "alarmmessage"

        /**
         * 数据表列
         */
        object Cols {
            /**
             * 主键，uuid
             */
            val ID = "id"
            /**
             * 设备名
             */
            val NAME = "name"
            /**
             * 报警信息
             */
            val MESSAGE = "message"

            /**
             * 报警时间
             */
            val TIME = "time"
        }
    }
}