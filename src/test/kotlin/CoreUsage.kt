import com.tencent.mobileqq.dt.model.FEBound
import net.mamoe.mirai.utils.BotConfiguration
import top.mrxiaom.qsign.QSignService
import java.io.File

fun setup() {
    // 设置签名服务路径
    val basePath = File("txlib/8.9.63").also {
        QSignService.Factory.basePath = it
    }
    // 初始化签名服务，加载配置文件
    FEBound.initAssertConfig(QSignService.Factory.basePath)
    QSignService.Factory.loadConfigFromFile(basePath.resolve("config.json"))

    // 设置签名cmd白名单，请改为读取 src/main/resources/cmd_whitelist.txt
    QSignService.Factory.cmdWhiteList = listOf("...")
    // 设置使用签名服务的协议列表
    // 必要时请使用 MiraiProtocol.ANDROID_PHONE.applyProtocolInfo(json) 从 json 加载协议变更
    QSignService.Factory.supportedProtocol = listOf(BotConfiguration.MiraiProtocol.ANDROID_PHONE)
    // 注册签名服务
    QSignService.Factory.register()
}
