package top.mrxiaom.qsign

import com.tencent.mobileqq.dt.model.FEBound
import kotlinx.serialization.json.Json
import moe.fuqiuluo.comm.QSignConfig
import moe.fuqiuluo.comm.checkIllegal
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import java.io.File

object PluginMain : KotlinPlugin(
    JvmPluginDescriptionBuilder(
        "top.mrxiaom.qsign", BuildConstants.VERSION
    ).apply {
        name("QSign")
        author("MrXiaoM")
    }.build()
) {
    lateinit var basePath: File
    lateinit var CONFIG: QSignConfig
    override fun PluginComponentStorage.onLoad() {
        PluginConfig.reload()
        basePath = File(PluginConfig.basePath)
        logger.info("Loading QSign v$version")
        logger.info("运行目录: ${basePath.absolutePath}")

        FEBound.initAssertConfig(basePath)
        CONFIG = Json.decodeFromString(
            QSignConfig.serializer(),
            basePath.resolve("config.json").readText()
        ).apply { checkIllegal() }
    }
}