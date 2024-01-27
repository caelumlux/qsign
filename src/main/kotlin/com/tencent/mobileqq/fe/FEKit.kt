package com.tencent.mobileqq.fe

import com.github.unidbg.linux.android.dvm.BaseVM
import com.tencent.mobileqq.channel.ChannelManager
import com.tencent.mobileqq.dt.Dtn
import com.tencent.mobileqq.qsec.qsecurity.DeepSleepDetector
import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.sign.QQSecuritySign
import moe.fuqiuluo.unidbg.QSecVM

object FEKit {
    fun init(vm: QSecVM, uin: String = "0") {
        if ("fekit" in vm.global) return
        vm.global["uin"] = uin

        if ("DeepSleepDetector" !in vm.global) {
            vm.global["DeepSleepDetector"] = DeepSleepDetector()
        }

        QQSecuritySign.initSafeMode(vm, false)
        QQSecuritySign.dispatchEvent(vm, "Kicked", uin)

        ChannelManager.setChannelProxy(vm, vm.newInstance("com/tencent/mobileqq/channel/ChannelProxy"))
        ChannelManager.initReport(vm, vm.envData.qua, "6.100.248") // TODO(maybe check?)

        val context = (vm.vm as BaseVM).resolveClass("android/content/Context", vm.vm.resolveClass("java/io/File")).newObject(null)

        Dtn.initContext(vm, context)

        Dtn.initLog(vm, vm.newInstance("com/tencent/mobileqq/fe/IFEKitLog"))
        Dtn.initUin(vm, uin)

        QSec.doSomething(vm, context)

    }

    fun changeUin(vm: QSecVM, uin: String) {
        vm.global["uin"] = uin

        Dtn.initUin(vm, uin)
        QQSecuritySign.dispatchEvent(vm, "Kicked", uin)
    }
}