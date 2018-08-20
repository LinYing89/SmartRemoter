package com.bairock.iot.smartremoter.settings

/**
 * 配置设备返回结果
 */
enum class ConfigResult(var code:Int, var msg : String) {
    SUCCESS(0, "配置成功"),
    NET_ERROR(1, "网络错误, 请检查网络名称或密码是否正确"),
    DEVICE_ERROR(2, "网络配置成功, 但设备无连接")

}