package moe.fuqiuluo.unidbg.session

import com.github.unidbg.worker.Worker
import com.github.unidbg.worker.WorkerPool
import top.mrxiaom.qsign.QSignService.Factory.Companion.CONFIG
import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.fe.FEKit
import kotlinx.coroutines.sync.Mutex
import moe.fuqiuluo.comm.EnvData
import moe.fuqiuluo.unidbg.QSecVM
import top.mrxiaom.qsign.QSignService

class Session(envData: EnvData) {
    internal val vm: QSecVM =
        QSecVM(QSignService.Factory.basePath, envData, CONFIG.unidbg.dynarmic, CONFIG.unidbg.unicorn, CONFIG.unidbg.kvm)
    internal val mutex = Mutex()

    init {
        vm.global["PACKET"] = arrayListOf<SsoPacket>()
        vm.global["mutex"] = Mutex(true)
        vm.global["qimei36"] = envData.qimei36.lowercase()
        vm.global["guid"] = envData.guid.lowercase()
        vm.init()
        FEKit.init(vm, envData.uin.toString())
    }
}