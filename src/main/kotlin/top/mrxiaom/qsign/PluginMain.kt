package top.mrxiaom.qsign

import com.tencent.mobileqq.dt.model.FEBound
import kotlinx.serialization.json.*
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.BotConfiguration
import top.mrxiaom.qsign.QSignService.Factory
import top.mrxiaom.qsign.QSignService.Factory.Companion.CONFIG
import java.io.File

object PluginMain : KotlinPlugin(
    JvmPluginDescriptionBuilder(
        "top.mrxiaom.qsign", BuildConstants.VERSION
    ).apply {
        name("QSign")
        author("MrXiaoM")
    }.build()
) {
    override fun PluginComponentStorage.onLoad() {
        PluginConfig.reload()
        val basePath = File(PluginConfig.basePath).also {
            Factory.basePath = it
        }
        logger.info("Loading QSign v$version (unidbg-fetch-qsign v${BuildConstants.UNIDBG_FETCH_QSIGN_VERSION})")
        logger.info("正在 Mirai ${MiraiConsole.version} 上运行")
        logger.info("签名服务目录: ${basePath.absolutePath}")

        FEBound.initAssertConfig(Factory.basePath)
        logger.info("FEBound sum = ${FEBound.checkCurrent()}")
        Factory.loadConfigFromFile(basePath.resolve("config.json"))

        logger.info("已成功读取签名服务配置")
        logger.info("  签名服务版本: ${CONFIG.protocol.version}")
        logger.info("  签名服务QUA: ${CONFIG.protocol.qua}")
        logger.info("=============================================")
        val supportedProtocol = mutableListOf<BotConfiguration.MiraiProtocol>()
        for (protocol in BotConfiguration.MiraiProtocol.values()) {
            val file = basePath.listFiles { it ->
                it.name.equals("$protocol.json", true)
            }?.firstOrNull() ?: continue
            if (file.exists()) {
                kotlin.runCatching {
                    val json = Json.parseToJsonElement(file.readText()).jsonObject
                    protocol.applyProtocolInfo(json)
                    supportedProtocol.add(protocol)
                    logger.info("已加载 $protocol 协议变更: ${protocol.status()}")
                }.onFailure {
                    logger.warning("加载 $protocol 的协议变更时发生一个异常", it)
                }
            }
        }
        if (supportedProtocol.isEmpty()) {
            logger.warning("你使用的签名服务目前没有支持的协议版本! 请将协议信息文件 (android_phone.json 或 android_pad.json) 置于“签名服务目录”，并重新启动 mirai")
            logger.warning("签名服务未注册")
            return
        } else {
            logger.info("支持签名服务的协议: ${supportedProtocol.joinToString(", ")}")
        }

        Factory.cmdWhiteList = getResource("cmd_whitelist.txt")?.lines() ?: error("`cmd_whitelist.txt` not found.")
        Factory.supportedProtocol = supportedProtocol
        Factory.register()
    }
}
