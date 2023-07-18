package top.mrxiaom.qsign

import com.tencent.mobileqq.channel.SsoPacket
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import moe.fuqiuluo.api.UnidbgFetchQSign
import moe.fuqiuluo.comm.QSignConfig
import moe.fuqiuluo.comm.checkIllegal
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.internal.spi.EncryptServiceContext
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_BOT_PROTOCOL
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_CHANNEL_PROXY
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_COMMAND_STR
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_DEVICE_INFO
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_QIMEI36
import net.mamoe.mirai.utils.*
import java.io.File
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
    @OptIn(MiraiInternalApi::class)
    override fun initialize(context: EncryptServiceContext) {
        val device = context.extraArgs[KEY_DEVICE_INFO]

        channel0 = context.extraArgs[KEY_CHANNEL_PROXY]

        logger.info("register ${context.id}")

        UnidbgFetchQSign.register(
            context.id,
            device.androidId.decodeToString(),
            device.guid.toUHexString(""),
            context.extraArgs[KEY_QIMEI36]
        )
    }

    override fun encryptTlv(
        context: EncryptServiceContext,
        tlvType: Int,
        payload: ByteArray
    ): ByteArray? {
        if (tlvType != 0x544) return null
        val command = context.extraArgs[KEY_COMMAND_STR]

        logger.info("energy $command")

        return runBlocking {
            UnidbgFetchQSign.customEnergy(
                context.id,
                command,
                payload
            )
        }
    }

    override fun qSecurityGetSign(
        context: EncryptServiceContext,
        sequenceId: Int,
        commandName: String,
        payload: ByteArray
    ): EncryptService.SignResult? {
        if (commandName == "StatSvc.register") {
            if (!token.get() && token.compareAndSet(false, true)) {
                launch(CoroutineName("RequestToken")) {
                    while (isActive) {
                        delay(2400000L)
                        val request = try {
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
            UnidbgFetchQSign.sign(uin = context.id, cmd = commandName, seq = sequenceId, buffer = payload)
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

                UnidbgFetchQSign.submit(uin = uin, cmd = result.cmd, callbackId = callback.callbackId, buffer = result.data)
            }
        }
    }

    companion object {
        private val logger = MiraiLogger.Factory.create(QSignService::class)
    }
    class Factory : EncryptService.Factory {
        override val priority: Int = -1919
        companion object {
            lateinit var supportedProtocol: List<BotConfiguration.MiraiProtocol>
            lateinit var basePath: File
            lateinit var CONFIG: QSignConfig
            lateinit var cmdWhiteList: List<String>
            private val json = Json {
                ignoreUnknownKeys = true
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
            if (context.extraArgs[KEY_BOT_PROTOCOL] !in supportedProtocol){
                throw UnsupportedOperationException("QSignService only supports ${supportedProtocol.joinToString(", ")}.")
            }
            return QSignService(serviceSubScope.coroutineContext)
        }
    }
}
