# QSign

[![mirai-core/mirai-console 2.15.0+](https://img.shields.io/badge/mirai--core/mirai--console-2.15.0+-green)](https://github.com/mamoe/mirai)
[![unidbg-fetch-qsign 1.1.4](https://img.shields.io/badge/unidbg--fetch--qsign-1.1.4-yellowgreen)](https://github.com/fuqiuluo/unidbg-fetch-qsign)
[![Releases](https://img.shields.io/github/downloads/MrXiaoM/unidbg-fetch-qsign/total?label=%E4%B8%8B%E8%BD%BD%E9%87%8F&logo=github)](https://github.com/MrXiaoM/unidbg-fetch-qsign/releases)
[![Stars](https://img.shields.io/github/stars/MrXiaoM/unidbg-fetch-qsign?label=%E6%A0%87%E6%98%9F&logo=github)](https://github.com/MrXiaoM/unidbg-fetch-qsign/stargazers)

通过 Unidbg 获取 QQSign 参数，基于 fuqiuluo/unidbg-fetch-qsign 修改，仅适用于 mirai。unidbg-fetch-sign 最低从 QQ8.9.33 (不囊括) 开始支持，TIM 不支持。

# 切记

 - 请使用与协议对应版本的 libfekit.so 文件
 - QSign 基于 Android 平台，其它平台 Sign 计算的参数不同，不互通（例如：IPad）。
 - 不支持载入 Tim.apk 的 so 文件。

# 待办事项

未完成，敬请期待

- [ ] initialize-register
  - [x] invoke api
  - [ ] check for possible deadlock issue
- [x] encryptTlv-customEnergy
- [ ] qSecurityGetSign
  - [x] sign
  - [x] request token
  - [ ] check for possible deadlock issue
  - [ ] etc...
- [ ] test
  - [x] login
  - [ ] send message

# 部署方法

请在 mirai 版本大于或等于 `2.15.0` **正式版**的环境下操作。

> 文件很大，下载可能较慢，可以尝试使用以下工具站进行加速  
> https://ghproxy.com/  
> https://github.moeyy.cn/  

到 [Releases](https://github.com/MrXiaoM/unidbg-fetch-qsign/releases) 下载 `qsign-x.x.x-all.zip`，将 `plugins` 和 `txlib` 文件夹解压到 mirai 所在目录。解压后文件夹结构大致情况如下
```
|-bots
|-config
|-data
|-plugins
  |-qsign-x.x.x.mirai2.jar
|-txlib
  |-8.9.58
  |-8.9.63
    |-android_phone.json
    |-config.json
    |-dtconfig.json
    |-libfekit.so
    |-libQSec.so
  |-8.9.68
```
首次加载插件会新建配置文件 `config/top.mrxiaom.qsign/config.yml`

目前配置文件里面仅有配置项 `base-path`，即使用的签名服务路径，默认值为 `txlib/8.9.63`，按自己所需进行修改。  
推荐使用默认值，目前 `8.9.58` **未测试**，`8.9.68` **没有合适的**设备信息文件。  

配置文件的修改在重启 mirai 后生效。

本仓库提供的包已自带协议，本插件加载时将会把协议信息应用到 mirai。  
启动 mirai 后检查一下日志输出的`协议版本`与`签名版本`是否一致，  
然后使用 `ANDROID_PHONE` 协议登录即可。

# 更改协议信息

插件加载时，会扫描 `base-path` 下以协议名命名的文件并加载协议变更。如 `android_phone.json`。  
本插件的签名服务仅支持加载了协议变更的协议使用。

# 在 mirai-core 中使用

先想办法引用本插件为依赖，在登录前执行以下代码  
[kotlin](src/test/kotlin/CoreUsage.kt)  
[java](src/test/java/CoreUsage.java)

构建的包稍大，投放到网络仓库上下载会很慢，请使用本地依赖。
