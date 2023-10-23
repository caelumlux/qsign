package top.mrxiaom.qsign

import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.dt.model.FEBound
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import moe.fuqiuluo.api.UnidbgFetchQSign
import moe.fuqiuluo.comm.QSignConfig
import moe.fuqiuluo.comm.checkIllegal
import moe.fuqiuluo.unidbg.session.SessionManager
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.internal.spi.EncryptServiceContext
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_BOT_PROTOCOL
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_CHANNEL_PROXY
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_COMMAND_STR
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_DEVICE_INFO
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_QIMEI36
import net.mamoe.mirai.utils.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class QSignService(
    coroutineContext: CoroutineContext
) : EncryptService, CoroutineScope {

    override val coroutineContext: CoroutineContext =
        coroutineContext + SupervisorJob(coroutineContext[Job]) + CoroutineExceptionHandler { context, e ->
            if (e !is CancellationException) {
                logger.warning("with ${context[CoroutineName]}", e)
            }
        }

    private var channel0: EncryptService.ChannelProxy? = null
    private val channel: EncryptService.ChannelProxy get() = channel0 ?: throw IllegalStateException("need initialize")
    private val token = java.util.concurrent.atomic.AtomicBoolean(false)
    data class RegisterData(
        val androidId: String,
        val guid: String,
        val qimei36: String
    )
    private val registerData = mutableMapOf<Long, RegisterData>()
    private fun checkReg(uin: Long) {
        val reg = registerData[uin]
        if (!SessionManager.contains(uin) && reg != null) {
            UnidbgFetchQSign.register(uin, reg.androidId, reg.guid, reg.qimei36)
        }
    }
    @OptIn(MiraiInternalApi::class)
    override fun initialize(context: EncryptServiceContext) {
        val device = context.extraArgs[KEY_DEVICE_INFO]

        channel0 = context.extraArgs[KEY_CHANNEL_PROXY]

        logger.info("register ${context.id}")

        registerData[context.id] = RegisterData(
            device.androidId.decodeToString(),
            device.guid.toUHexString(""),
            context.extraArgs[KEY_QIMEI36]
        ).apply {
            UnidbgFetchQSign.register(context.id, androidId, guid, qimei36)
        }
    }

    override fun encryptTlv(
        context: EncryptServiceContext,
        tlvType: Int,
        payload: ByteArray
    ): ByteArray? {
        if (tlvType != 0x544) return null
        val command = context.extraArgs[KEY_COMMAND_STR]

        logger.info("energy $command")

        val reg = registerData[context.id]
        return runBlocking {
            UnidbgFetchQSign.customEnergy(
                context.id,
                command,
                payload,
                reg?.androidId ?: "",
                reg?.guid ?: "",
                reg?.qimei36 ?: ""
            )
        }
    }

    override fun qSecurityGetSign(
        context: EncryptServiceContext,
        sequenceId: Int,
        commandName: String,
        payload: ByteArray
    ): EncryptService.SignResult? {

        checkReg(context.id)

        if (commandName == "StatSvc.register") {
            if (!token.get() && token.compareAndSet(false, true)) {
                launch(CoroutineName("RequestToken")) {
                    while (isActive) {
                        delay(2400000L)
                        val request = try {
                            checkReg(context.id)
                            UnidbgFetchQSign.requestToken(context.id)
                        } catch (cause: Throwable) {
                            logger.error(cause)
                            continue
                        }
                        if (request.first) callback(uin = context.id, request = request.second)
                    }
                }
            }
        }
        if (commandName !in Factory.cmdWhiteList) return null

        logger.verbose("sign $commandName")
        val data = runBlocking {
            val reg = registerData[context.id]
            UnidbgFetchQSign.sign(
                uin = context.id,
                cmd = commandName,
                seq = sequenceId,
                buffer = payload,
                androidId = reg?.androidId ?: "",
                guid = reg?.guid ?: "",
                qimei36 = reg?.qimei36 ?: ""
            )
        }

        callback(uin = context.id, request = data.requestCallback)

        return EncryptService.SignResult(
            sign = data.sign,
            token = data.token,
            extra = data.extra
        )
    }

    private fun callback(uin: Long, request: List<SsoPacket>) {
        launch(CoroutineName("SendMessage")) {
            for (callback in request) {
                logger.verbose("Bot(${uin}) sendMessage ${callback.cmd} ")
                val result = channel.sendMessage(
                    remark = "mobileqq.msf.security",
                    commandName = callback.cmd,
                    uin = 0,
                    data = callback.body.hexToBytes()
                )
                if (result == null) {
                    logger.debug("${callback.cmd} ChannelResult is null")
                    continue
                }
                checkReg(uin)
                UnidbgFetchQSign.submit(uin = uin, cmd = result.cmd, callbackId = callback.callbackId, buffer = result.data)
            }
        }
    }

    companion object {
        internal val logger = MiraiLogger.Factory.create(QSignService::class)
    }
    class Factory : EncryptService.Factory {
        override val priority: Int = -1919
        companion object {
            @JvmField
            var supportedProtocol: MutableSet<BotConfiguration.MiraiProtocol> = mutableSetOf()
            lateinit var basePath: File
            lateinit var CONFIG: QSignConfig
            @JvmField
            var cmdWhiteList: List<String> = this::class.java.classLoader
                .getResourceAsStream("cmd_whitelist.txt")
                ?.use { it.readBytes() }
                ?.toString(Charsets.UTF_8)
                ?.lines()
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() && !it.startsWith("#") }
                ?: listOf()
            private val json = Json {
                ignoreUnknownKeys = true
            }
            @JvmStatic
            fun init(basePath: File): Int {
                this.basePath = basePath

                when {
                    !basePath.exists() -> FileNotFoundException("设定的签名服务目录不存在")
                    !basePath.isDirectory -> IOException("目标路径不是目录")
                    !basePath.resolve("config.json").exists() -> FileNotFoundException("找不到 config.json")
                    !basePath.resolve("dtconfig.json").exists() -> FileNotFoundException("找不到 dtconfig.json")
                    !basePath.resolve("libfekit.so").exists() -> FileNotFoundException("找不到 libfekit.so")
                    //!basePath.resolve("libQSec.so").exists() -> FileNotFoundException("找不到 libQSec.so")
                    else -> null
                }?.also { throw it }

                FEBound.initAssertConfig(Factory.basePath)
                val sum = FEBound.checkCurrent()
                loadConfigFromFile(basePath.resolve("config.json"))
                return sum
            }
            @JvmStatic
            fun loadProtocols(dir: File? = null) {
                val basePath = dir ?: this.basePath
                for (protocol in BotConfiguration.MiraiProtocol.values()) {
                    val file = basePath.listFiles { it ->
                        it.name.equals("$protocol.json", true)
                    }?.firstOrNull() ?: continue
                    if (file.exists()) {
                        kotlin.runCatching {
                            val json = Json.parseToJsonElement(file.readText()).jsonObject
                            protocol.applyProtocolInfo(json)
                            supportedProtocol.add(protocol)
                        }.onFailure {
                            logger.warning("加载 $protocol 的协议变更时发生一个异常", it)
                        }
                    }
                }
            }

            @JvmStatic
            fun loadConfigFromFile(file: File) {
                CONFIG = json.decodeFromString(
                    QSignConfig.serializer(),
                    file.readText()
                ).apply { checkIllegal() }
            }
            @JvmStatic
            fun register() {
                Services.register(EncryptService.Factory::class.qualifiedName!!, Factory::class.qualifiedName!!, ::Factory)
                logger.info("已注册加密算法提供服务")
            }
        }
        override fun createForBot(context: EncryptServiceContext, serviceSubScope: CoroutineScope): EncryptService {
            if (supportedProtocol.isEmpty()) {
                throw UnsupportedOperationException("QSignService 没有找到可用的协议")
            }
            if (context.extraArgs[KEY_BOT_PROTOCOL] !in supportedProtocol){
                throw UnsupportedOperationException("QSignService 仅支持使用 ${supportedProtocol.joinToString(", ")} 协议登录.")
            }
            return QSignService(serviceSubScope.coroutineContext)
        }
    }
}
