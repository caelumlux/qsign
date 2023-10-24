package moe.fuqiuluo.unidbg.session

import com.github.unidbg.worker.WorkerPool
import com.github.unidbg.worker.WorkerPoolFactory
import moe.fuqiuluo.comm.EnvData
import top.mrxiaom.qsign.QSignService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

object SessionManager {
    private val sessionMap = ConcurrentHashMap<Long, Session>()

    operator fun get(uin: Long): Session? {
        return sessionMap[uin]
    }

    operator fun contains(uin: Long) = sessionMap.containsKey(uin)

    fun register(envData: EnvData) {
        if (envData.uin in this) {
            close(envData.uin)
        }
        sessionMap[envData.uin] = Session(envData)
    }

    fun close(uin: Long) {
        sessionMap[uin]?.vm?.destroy()
        sessionMap.remove(uin)
    }
}