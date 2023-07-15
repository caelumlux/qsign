package top.mrxiaom.qsign

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.hexToBytes
import net.mamoe.mirai.utils.toUHexString
import java.text.SimpleDateFormat
import java.util.*

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun BotConfiguration.MiraiProtocol.applyProtocolInfo(json: JsonObject) {
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
fun BotConfiguration.MiraiProtocol.status(): String {
    val impl = net.mamoe.mirai.internal.utils.MiraiProtocolInternal.protocols[this] ?: return "INVALID PROTOCOL"
    val buildTime = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(Date(impl.buildTime * 1000L))
    return "${impl.buildVer} ($buildTime)"
}
