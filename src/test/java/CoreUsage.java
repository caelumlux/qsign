import top.mrxiaom.qsign.QSignService;
import com.tencent.mobileqq.dt.model.FEBound;
import java.io.File;
import java.util.ArrayList;

public class CoreUsage {
    public static void setup() {
        // 设置签名服务路径
        File basePath = new File("txlib/8.9.63");
        QSignService.Factory.basePath = basePath;

        // 初始化签名服务，加载配置文件
        FEBound.initAssertConfig(QSignService.Factory.basePath);
        QSignService.Factory.loadConfigFromFile(new File(basePath, "config.json"));

        // 设置签名cmd白名单，请改为读取 src/main/resources/cmd_whitelist.txt
        QSignService.Factory.cmdWhiteList = new ArrayList<>();
        // 设置使用签名服务的协议列表
        // 必要时请使用 BotProtocolKt.applyProtocolInfo(MiraiProtocol.ANDROID_PHONE, Json.Default.parseToJsonElement(json)); 从 json 加载协议变更
        QSignService.Factory.supportedProtocol = new ArrayList<>();
        // 注册签名服务
        QSignService.Factory.register();
    }
}
