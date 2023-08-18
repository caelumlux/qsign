import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.mamoe.mirai.utils.BotConfiguration
import top.mrxiaom.qsign.QSignService
import top.mrxiaom.qsign.applyProtocolInfo
import java.io.File

/**
 * 安装签名服务示例
 */
fun setup() {
    QSignService.Factory.apply {
        // 初始化签名服务，参数为签名服务工作目录，里面应当包含
        // config.json, dtconfig.json, libfekit.so, libQSec.so
        init(File("txlib/8.9.63"))
        // 加载签名服务所需协议信息，如果你的协议信息存在非 basePath 的文件夹，请将参数改为协议信息所在目录
        // 该方法将会扫描目录下以协议信息命名的文件进行加载，如 android_phone.json
        // 如果你想单独加载协议信息文件，详见 loadProtocolExample() 中的例子
        loadProtocols()

        // 注册签名服务
        register()
    }
}
/**
 * 单独加载协议信息文件示例
 */
fun loadProtocolExample() {
    val protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
    val json = ""
    val jsonObject = Json.parseToJsonElement(json).jsonObject

    protocol.applyProtocolInfo(jsonObject)

    QSignService.Factory.supportedProtocol.add(protocol)
}
