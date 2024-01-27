package moe.fuqiuluo.unidbg.session

import CONFIG
import com.github.unidbg.worker.WorkerPool
import com.github.unidbg.worker.WorkerPoolFactory
import moe.fuqiuluo.api.BlackListError
import moe.fuqiuluo.comm.EnvData
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

object SessionManager {
    private var workerPoolMap = hashMapOf<String, WorkerPool>()
    private val envDataByUin = ConcurrentHashMap<Long, EnvData>()
    private val uin0 ="0";
    init {
        if (CONFIG.shareToken) {
            timer("reload", false, 1000L * 60 * 40, 1000L * 60 * 40) {
                workerPoolMap[uin0]?.close()
            }
        }
    }

    fun get(uin: Long): Session? {
        if (uin !in this) {
            return null
        }

        if (CONFIG.shareToken) {
            val envData = envDataByUin[uin]!!
            return workerPoolMap[uin.toString()]?.borrow<Session?>(3 * 1000L, TimeUnit.MILLISECONDS).also {
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
            return workerPoolMap[uin.toString()]?.borrow(3 * 1000L, TimeUnit.MILLISECONDS)
        }
    }

    operator fun contains(uin: Long) = envDataByUin.containsKey(uin)

    fun register(envData: EnvData) {
        if (CONFIG.blackList?.contains(envData.uin) == true) {
            throw BlackListError
        }
        if (envData.uin in this && !CONFIG.shareToken) {
            close(envData.uin)
        }

        envDataByUin[envData.uin] = envData
        if(workerPoolMap.containsKey(uin0) && CONFIG.shareToken) {
           return
        }
        workerPoolMap[if (CONFIG.shareToken) uin0 else envData.uin.toString()] = WorkerPoolFactory.create({ pool ->
            Session(envData, pool)
        }, CONFIG.count)
    }

    fun close(uin: Long) {
        envDataByUin.remove(uin)
        if (!CONFIG.shareToken) {
            workerPoolMap[uin.toString()]?.close()
        }
    }
}