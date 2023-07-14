package top.mrxiaom.qsign

import kotlinx.coroutines.*
import moe.fuqiuluo.api.UnidbgFetchQSign
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.internal.spi.EncryptServiceContext
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_BOT_PROTOCOL
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_COMMAND_STR
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_DEVICE_INFO
import net.mamoe.mirai.internal.spi.EncryptServiceContext.Companion.KEY_QIMEI36
import net.mamoe.mirai.utils.*
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
    
    @OptIn(MiraiInternalApi::class)
    override fun initialize(context: EncryptServiceContext) {
        val device = context.extraArgs[KEY_DEVICE_INFO]
        UnidbgFetchQSign.register(
            context.id,
            device.androidId.decodeToString(),
            device.guid.toUHexString(""),
            context.extraArgs[KEY_QIMEI36]
        )
    }

    override fun encryptTlv(context: EncryptServiceContext, tlvType: Int, payload: ByteArray): ByteArray? {
        if (tlvType != 0x544) return null
        return runBlocking {
            UnidbgFetchQSign.customEnergy(
                context.id,
                context.extraArgs[KEY_COMMAND_STR],
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
            TODO()
        }
        if (commandName !in cmdWhiteList) return null
        TODO("Not yet implemented")
    }


    companion object {
        private val logger = MiraiLogger.Factory.create(QSignService::class)
        lateinit var cmdWhiteList: List<String>
    }
    class Factory : EncryptService.Factory {
        override val priority: Int = -1919
        companion object {
            private val supportedProtocol = listOf(
                BotConfiguration.MiraiProtocol.ANDROID_PHONE,
                BotConfiguration.MiraiProtocol.ANDROID_PAD
            )
            fun register() {
                Services.register(EncryptService.Factory::class.qualifiedName!!, Factory::class.qualifiedName!!, ::Factory)
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
