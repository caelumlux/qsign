package moe.fuqiuluo.unidbg.env.files

fun fetchStatus(pid: Int): String {
    return """
        Name:	encent.mobileqq
        Umask:	0077
        State:	R (running)
        Tgid:	$pid
        Ngid:	0
        Pid:	$pid
        PPid:	852
        TracerPid:	0
        Uid:	10251	10251	10251	10251
        Gid:	10251	10251	10251	10251
        FDSize:	4096
        Groups:	3001 3002 3003 9997 20251 50251 99909997 
        VmPeak:	52344948 kB
        VmSize:	50430668 kB
        VmLck:	       0 kB
        VmPin:	       0 kB
        VmHWM:	  669512 kB
        VmRSS:	  248636 kB
        RssAnon:	  113156 kB
        RssFile:	  130144 kB
        RssShmem:	    5336 kB
        VmData:	 6597232 kB
        VmStk:	    8192 kB
        VmExe:	      16 kB
        VmLib:	  319280 kB
        VmPTE:	    4380 kB
        VmSwap:	  288924 kB
        CoreDumping:	0
        THP_enabled:	0
        Threads:	265
        SigQ:	1/28913
        SigPnd:	0000000000000000
        ShdPnd:	0000000000000000
        SigBlk:	0000000080001200
        SigIgn:	0000000000001001
        SigCgt:	0000006e400086fc
        CapInh:	0000000000000000
        CapPrm:	0000000000000000
        CapEff:	0000000000000000
        CapBnd:	0000000000000000
        CapAmb:	0000000000000000
        NoNewPrivs:	0
        Seccomp:	2
        Speculation_Store_Bypass:	thread vulnerable
        Cpus_allowed:	07
        Cpus_allowed_list:	0-2
        Mems_allowed:	1
        Mems_allowed_list:	0
        voluntary_ctxt_switches:	49881
        nonvoluntary_ctxt_switches:	10179
    """.trimIndent()
}
