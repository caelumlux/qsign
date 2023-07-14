package top.mrxiaom.qsign

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object PluginConfig : ReadOnlyPluginConfig("config") {
    @ValueName("base-path")
    @ValueDescription("unidbg-fetch-qsign 运行路径 (可填写相对于 mirai 运行目录的路径，或者绝对路径)")
    val basePath by value("txlib/8.9.63")
}