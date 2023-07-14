package top.mrxiaom.qsign

import com.tencent.mobileqq.dt.model.FEBound
import kotlinx.serialization.json.*
import moe.fuqiuluo.comm.QSignConfig
import moe.fuqiuluo.comm.checkIllegal
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.hexToBytes
import net.mamoe.mirai.utils.toUHexString
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

        for (protocol in BotConfiguration.MiraiProtocol.values()) {
            val file = basePath.resolve("$protocol.json")
            if (file.exists()) {
                val json = Json.parseToJsonElement(file.readText()).jsonObject
                protocol.applyProtocolInfo(json)
                logger.info("已加载 $protocol 协议变更: ${protocol.status()}")
            }
        }

        QSignService.Factory.register()
    }
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
private fun BotConfiguration.MiraiProtocol.applyProtocolInfo(json: JsonObject) {
    net.mamoe.mirai.internal.utils.MiraiProtocolInternal.protocols.compute(this) { _, impl ->
        impl?.apply {
                apkId = json.getValue("apk_id").jsonPrimitive.content
                id = json.getValue("app_id").jsonPrimitive.long
                buildVer = json.getValue("sort_version_name").jsonPrimitive.content
                ver = buildVer.substringBeforeLast(".")
                sdkVer = json.getValue("sdk_version").jsonPrimitive.content
                miscBitMap = json.getValue("misc_bitmap").jsonPrimitive.int
                subSigMap = json.getValue("sub_sig_map").jsonPrimitive.int
                mainSigMap = json.getValue("main_sig_map").jsonPrimitive.int
                sign = json.getValue("apk_sign").jsonPrimitive.content.hexToBytes().toUHexString(" ")
                buildTime = json.getValue("build_time").jsonPrimitive.long
                ssoVersion = json.getValue("sso_version").jsonPrimitive.int
                appKey = json.getValue("app_key").jsonPrimitive.content
        }
    }
}
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
private fun BotConfiguration.MiraiProtocol.status(): String {
    val impl = net.mamoe.mirai.internal.utils.MiraiProtocolInternal.protocols[this] ?: return "UNKNOWN"
    return "${impl.buildVer} (${impl.buildTime})"
}
