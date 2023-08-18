import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonElementKt;
import kotlinx.serialization.json.JsonObject;
import net.mamoe.mirai.utils.BotConfiguration;
import top.mrxiaom.qsign.BotProtocolKt;
import top.mrxiaom.qsign.QSignService;
import java.io.File;

public class CoreUsage {
    /**
     * 安装签名服务示例
     */
    public static void setup() {
        // 初始化签名服务，参数为签名服务工作目录，里面应当包含
        // config.json, dtconfig.json, libfekit.so, libQSec.so
        QSignService.Factory.init(new File("txlib/8.9.63"));
        // 加载签名服务所需协议信息，如果你的协议信息存在非 以上的工作目录 中的文件夹，请将参数 null 改为协议信息所在目录
        // 该方法将会扫描目录下以协议信息命名的文件进行加载，如 android_phone.json
        // 如果你想单独加载协议信息文件，详见 loadProtocolExample() 中的例子
        QSignService.Factory.loadProtocols(null);

        // 注册签名服务
        QSignService.Factory.register();
    }

    /**
     * 单独加载协议信息文件示例
     */
    public static void loadProtocolExample() {
        BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
        String json = "";
        JsonObject jsonObject = JsonElementKt.getJsonObject(Json.Default.parseToJsonElement(json));

        BotProtocolKt.applyProtocolInfo(BotConfiguration.MiraiProtocol.ANDROID_PHONE, jsonObject);

        QSignService.Factory.supportedProtocol.add(protocol);
    }
}
