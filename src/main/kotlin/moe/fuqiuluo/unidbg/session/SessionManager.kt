package moe.fuqiuluo.unidbg.session

import com.github.unidbg.worker.WorkerPool
import com.github.unidbg.worker.WorkerPoolFactory
import moe.fuqiuluo.comm.EnvData
import top.mrxiaom.qsign.QSignService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

object SessionManager {
    private var workerPoolMap = hashMapOf<Long, WorkerPool>()
    private val envDataByUin = ConcurrentHashMap<Long, EnvData>()

    init {
        if (QSignService.Factory.CONFIG.shareToken) {
            timer("reload", false, 1000L * 60 * 40, 1000L * 60 * 40) {
                workerPoolMap[0]?.close()
            }
        }
    }

    fun get(uin: Long): Session? {
        if (uin !in this) {
            return null
        }

        if (QSignService.Factory.CONFIG.shareToken) {
            val envData = envDataByUin[uin]!!
            return workerPoolMap[0]?.borrow<Session?>(60 * 1000L, TimeUnit.MILLISECONDS).also {
                val env = it?.vm?.envData
                if (env != null && env.uin != uin) {
                    env.uin = uin
                    env.androidId = envData.androidId
                    env.code = envData.code
                    env.guid = envData.guid
                    env.packageName = envData.packageName
                    env.qua = envData.qua
                    env.version = envData.version
                    env.qimei36 = envData.qimei36
                }
            }
        } else {
            return workerPoolMap[uin]?.borrow(60 * 1000L, TimeUnit.MILLISECONDS)
        }
    }

    private fun WorkerPool?.isWorkerStopped(): Boolean {
        return if (this == null) return true else try {
            val field = this::class.java.getField("stopped")
            field.isAccessible = true
            field.get(this) as Boolean
        } catch (ignored: Throwable) {
            true
        }
    }

    operator fun contains(uin: Long) = envDataByUin.containsKey(uin)

    fun register(envData: EnvData) {
        if (QSignService.Factory.CONFIG.blackList?.contains(envData.uin) == true) {
            error("${envData.uin} is in blacklist")
        }
        if (envData.uin in this && !QSignService.Factory.CONFIG.shareToken) {
            close(envData.uin)
        }

        envDataByUin[envData.uin] = envData
        val stopped = workerPoolMap[if (QSignService.Factory.CONFIG.shareToken) 0 else envData.uin].isWorkerStopped()
        if (stopped || !(workerPoolMap.containsKey(0) && QSignService.Factory.CONFIG.shareToken)) {
            workerPoolMap[if (QSignService.Factory.CONFIG.shareToken) 0 else envData.uin] = WorkerPoolFactory.create({ pool ->
                Session(envData, pool)
            }, QSignService.Factory.CONFIG.count)
        }
    }

    fun close(uin: Long) {
        envDataByUin.remove(uin)
        if (!QSignService.Factory.CONFIG.shareToken) {
            workerPoolMap[uin]?.close()
        }
    }
}