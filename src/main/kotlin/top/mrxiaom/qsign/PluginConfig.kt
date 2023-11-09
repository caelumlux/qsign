package top.mrxiaom.qsign

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object PluginConfig : ReadOnlyPluginConfig("config") {
    @ValueName("base-path")
    @ValueDescription("unidbg-fetch-qsign 运行路径 (可填写相对于 mirai 运行目录的路径，或者绝对路径)")
    // https://github.com/mamoe/mirai/tree/dev/mirai-console/tools/intellij-plugin#plugindata
    @Suppress("READ_ONLY_VALUE_CANNOT_BE_VAR")
    var basePath by value("txlib/8.9.63")
    @ValueName("ignore-termux")
    @ValueDescription("""
       忽略环境为 Termux，强制加载签名服务
       部分人可以在 Termux 中使用，部分人无法通过 Termux 使用
       如果你执意要在 Termux 中使用本插件，请开启本选项
       """)
    val ignoreTermux by value(false)
    @ValueName("app_install_folder")
    val appInstallFolder by value("/data/app/~~nNzv5koU9DgkrbtCpa02wQ==/\${packageName}-fR9VqAFGIZNVZ8MgZYh0Ow==")
    @ValueName("screen_size_width")
    val screenSizeWidth by value(1080)
    @ValueName("screen_size_height")
    val screenSizeHeight by value(2400)
    @ValueName("density")
    val density by value("2.75")
    @ValueName("serial_number")
    @ValueDescription("""
       /sys/devices/soc0/serial_number
       """)
    val serialNumber by value("0x0000043be8571339")
    @ValueName("android_version")
    val androidVersion by value("13")
    @ValueName("android_sdk_version")
    val androidSdkVersion by value(33)
    @ValueName("target_sdk_version")
    val targetSdkVersion by value(29)
    @ValueName("storage_size")
    val storageSize by value("137438953471")

    @OptIn(ConsoleExperimentalApi::class)
    override fun onInit(owner: PluginDataHolder, storage: PluginDataStorage) {
        super.onInit(owner, storage)
        pushToCommonConfig()
    }
    private fun pushToCommonConfig() = CommonConfig.also {
        it.appInstallFolder = appInstallFolder
        it.screenSizeWidth = screenSizeWidth
        it.screenSizeHeight = screenSizeHeight
        it.density = density
        it.serialNumber = serialNumber
        it.androidVersion = androidVersion
        it.androidSdkVersion = androidSdkVersion
        it.targetSdkVersion = targetSdkVersion
        it.storageSize = storageSize
    }
}