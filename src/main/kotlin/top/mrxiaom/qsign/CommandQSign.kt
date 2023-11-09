package top.mrxiaom.qsign

import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.plugin.jvm.savePluginConfig
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import java.io.File

object CommandQSign : SimpleCommand(
    owner = PluginMain,
    primaryName = "qsign",
    description = "更改签名服务版本"
) {
    @Handler
    @OptIn(ConsoleExperimentalApi::class)
    suspend fun change(context: CommandContext, @Name("版本号") version: String? = null) {
        if (version == null) {
            context.sender.sendMessage(
                "可用签名服务版本列表:\n" +
                File("txlib").run {
                    if (!exists()) return@run "  (无)"
                    "  " + listFiles()?.filter { it.isDirectory }?.joinToString("\n  ") { it.name }
                }
            )
        } else {
            if (!File("txlib/$version").exists()) {
                context.sender.sendMessage("找不到该版本，使用 /qsign 命令可查看版本列表");
                return
            }
            PluginConfig.basePath = "txlib/$version"
            PluginMain.savePluginConfig(PluginConfig)
            context.sender.sendMessage("设置成功! 重启 mirai 后生效")
        }
    }
}