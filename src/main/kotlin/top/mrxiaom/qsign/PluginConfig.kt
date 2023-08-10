package top.mrxiaom.qsign

import net.mamoe.mirai.console.data.*

object PluginConfig : ReadOnlyPluginConfig("config") {
    @ValueName("base-path")
    @ValueDescription("unidbg-fetch-qsign 运行路径 (可填写相对于 mirai 运行目录的路径，或者绝对路径)")
    // https://github.com/mamoe/mirai/tree/dev/mirai-console/tools/intellij-plugin#plugindata
    @Suppress("READ_ONLY_VALUE_CANNOT_BE_VAR")
    var basePath by value("txlib/8.9.63")
}