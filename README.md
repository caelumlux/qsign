# QSign

[![mirai-core/mirai-console 2.15.0+](https://img.shields.io/badge/mirai--core/mirai--console-2.15.0+-green)](https://github.com/mamoe/mirai)
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

下载并安装本插件，首次加载插件会生成配置文件 `config/top.mrxiaom.qsign/config.yml`，并且因为无法找到签名所需配置文件而崩溃。  
目前仅有配置项 `base-path`，默认值为 `txlib/8.9.63`。  
到 `Releases` 下载 `txlib-pack.zip`，将 `txlib` 文件夹解压到 mirai 所在目录。解压后文件夹结构大致情况如下
```
|-bots
|-config
|-data
|-plugins
  |-qsign-x.x.x.mirai2.jar
|-txlib
  |-8.9.58
  |-8.9.63
  |-8.9.68
```
按照你需要的协议版本修改 `base-path` 的值然后启动 mirai。  
本仓库提供的 `txlib-pack.zip` 已自带协议，会在加载本插件时将协议信息应用到 mirai。  
启动 mirai 后检查一下日志输出的`协议版本`与`签名版本`是否一致，  
使用 `ANDROID_PHONE` 协议登录即可。
