package top.mrxiaom.qsign

import moe.fuqiuluo.api.UnidbgFetchQSign
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.globalEventChannel
import top.mrxiaom.qsign.QSignService.Factory
import top.mrxiaom.qsign.QSignService.Factory.Companion.CONFIG
import java.io.File
import kotlin.system.exitProcess

object PluginMain : KotlinPlugin(
    JvmPluginDescriptionBuilder(
        "top.mrxiaom.qsign", BuildConstants.VERSION
    ).apply {
        name("QSign")
        author("MrXiaoM")
    }.build()
) {
    val isTermux = File("/data/data/com.termux").exists()
    override fun PluginComponentStorage.onLoad() {
        CommonConfig.virtualRootPath = dataFolder
        PluginConfig.reload()
        PluginConfig.save()
        if (isTermux && !PluginConfig.ignoreTermux) {
            logger.warning("本插件不支持在 Termux 中运行，请尝试使用 fix-protocol-version")
            logger.warning("若执意想在 Termux 中使用本插件，请到 config/top.mrxiaom.qsign/config.yml 将 ignore-termux 开启")
            exitProcess(1)
        }
        if (Factory.cmdWhiteList.isEmpty()) {
            logger.warning("cmd whitelist 为空，签名服务将停止加载")
            return
        }
        val basePath = File(PluginConfig.basePath)

        logger.info("Loading QSign v$version (unidbg-fetch-qsign v${BuildConstants.UNIDBG_FETCH_QSIGN_VERSION})")
        logger.info("正在 Mirai ${MiraiConsole.version} 上运行")
        logger.info("签名服务目录: ${basePath.absolutePath}")

        val sum = Factory.init(basePath)
        logger.info("FEBound sum = $sum")

        logger.info("已成功读取签名服务配置")
        logger.info("  签名协议包名: ${CONFIG.protocol.packageName ?: "com.tencent.mobileqq"}")
        logger.info("  签名服务版本: ${CONFIG.protocol.version}")
        logger.info("  签名服务QUA: ${CONFIG.protocol.qua}")
        logger.info("=============================================")

        Factory.loadProtocols()
        if (Factory.supportedProtocol.isEmpty()) {
            logger.warning("你使用的签名服务目前没有支持的协议版本! 请将协议信息文件 (android_phone.json 或 android_pad.json) 置于“签名服务目录”，并重新启动 mirai")
            logger.warning("签名服务未注册")
            return
        } else {
            Factory.supportedProtocol.forEach {
                PluginMain.logger.info("已加载 $it 协议变更: ${it.status()}")
            }
            logger.info("支持签名服务的协议: ${Factory.supportedProtocol.joinToString(", ")}")
        }

        Factory.register()

        globalEventChannel().run {
            subscribeAlways<BotOfflineEvent> {
                UnidbgFetchQSign.destroy(it.bot.id)
                QSignService.logger.info("destroy ${it.bot.id}")
            }
        }
    }

    override fun onEnable() {
        if (!isTermux || PluginConfig.ignoreTermux) {
            CommandQSign.register()
        }
    }
}
