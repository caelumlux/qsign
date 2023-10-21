# 快信号捕获工程 quick-signal fetch project

> 勤劳备份，在拉闸之前自食其力吧。

[![未来核心/未来控制台 2.15.0+](https://img.shields.io/badge/%E6%9C%AA%E6%9D%A5%E6%A0%B8%E5%BF%83/%E6%9C%AA%E6%9D%A5%E6%8E%A7%E5%88%B6%E5%8F%B0-2.15.0+-blue)](https://github.com/mamoe/mirai)
[![Releases](https://img.shields.io/github/downloads/MrXiaoM/qsign/total?label=%E4%B8%8B%E8%BD%BD%E9%87%8F&logo=github)](https://github.com/MrXiaoM/qsign/releases)
[![Stars](https://img.shields.io/github/stars/MrXiaoM/qsign?label=%E6%A0%87%E6%98%9F&logo=github)](https://github.com/MrXiaoM/qsign/stargazers)

通过 Unidbg 获取签名参数，仅适用于未来。最低从 8.9.33 (不囊括) 开始支持，TIM 不支持。

本插件**自带**协议信息，在加载时将会应用与签名服务版本相同的设备信息文件。

# 不支持特缪斯

特缪斯请使用 [bak-fpv](https://github.com/MrXiaoM/qsign/tree/bak-fpv)，并在电脑上搭建签名服务，通过局域网连接签名服务。

# 切记

 - 请使用与协议对应版本的 libfekit.so 文件
 - 基于 Android 平台，其它平台计算的参数不同，不互通（例如：iPad）。

# 利弊

优点：内置签名服务无需开放端口，配置方便，即装即用，适合只开一个未来的大部分轻度用户使用。自带协议信息，更换版本时无需反复下载。

缺点：服务崩溃时会连带未来一起崩溃，支持多 bot 但不支持多未来实例，在开启多个未来的情况下每个未来一个签名服务，会比外置签名服务占用更多运行内存。

# 开发

由于项目接口差异，本项目提交信息以 `[sync]` 开头的提交均为同步**源仓库**中相应的提交。

你可以在 [动作](https://github.com/MrXiaoM/qsign/actions) 找到最新构建。

~~有别的项目要做，本项目进度暂缓，不定期同步上游仓库的变更。~~

# 部署方法

请在未来版本大于或等于 `2.15.0` **正式版**的环境下操作。

在操作之前，请先删除原有的`所有`**可能带有签名服务**的插件，以免产生冲突。

> 文件很大，下载可能较慢，可以尝试使用以下工具站进行加速  
> https://ghproxy.com/  
> https://ghproxy.net/  

到 [瑞丽斯](https://github.com/MrXiaoM/qsign/releases) 下载 `qsign-x.x.x-all.zip`，将 `plugins` 和 `txlib` 文件夹解压到未来所在目录。解压后文件夹结构大致情况如下
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
    |-android_pad.json
    |-config.json
    |-dtconfig.json
    |-libfekit.so
    |-libQSec.so
  |-8.9.68
  |-8.9.70
  |-8.9.71
  |-8.9.73
  |-8.9.76
  |-8.9.78
  |-8.9.80
  |-8.9.83
```

通常来说，**到这步就完成了**，你可以无脑使用 `ANDROID_PHONE` 或 `ANDROID_PAD` 协议登录了。

如果需要详细配置，请往下看。

# 详细配置(非必要)

首次加载插件会新建配置文件 `config/top.mrxiaom.qsign/config.yml`

目前配置文件里面仅有配置项 `base-path`，即使用的签名服务路径，默认值为 `txlib/8.9.63`，按自己所需进行修改。  
推荐使用默认值，目前本人测试 `8.9.63` 运行稳定，`8.9.58`、`8.9.68`、`8.9.70` 以及之后的版本 **未测试，风险自担。**  

配置文件的修改在重启未来后生效。

本仓库提供的包已自带协议，本插件加载时将会把协议信息应用到 mirai。  
启动未来后检查一下日志输出的`协议版本`与`签名版本`是否一致，  
然后使用 `ANDROID_PHONE` 协议登录即可。

# 更改协议信息

插件加载时，会扫描 `base-path` 目录下以协议名命名的文件，并加载协议变更。如 `android_phone.json`。  
本插件的签名服务仅支持加载了协议变更的协议使用。

# 升级插件

如果你要升级 qsign 插件，通常只需要更换 plugins 文件夹内的 `qsign-x.x.x.mirai2.jar` 即可。  
有时 `txlib` 内的签名服务相关文件会变动，可按需决定是否更新。

# 命令

```
/qsign          # 查看可用的签名服务版本列表
/qsign [版本号]  # 更改签名服务版本(重启生效)
```

# 屏蔽/减少日志
> 仅限未来控制台，如果你在使用未来核心并且没有使用未来记录仪作为日志框架，请根据自己使用的日志框架自行想办法解决。

通常我们会看到日志的输出格式如下
```
年-月-日 时:分:秒 I/名称: 内容
例如
2023-07-16 11:45:14 I/QSign: Hello World!
```
在 **未来关闭的情况下** 编辑配置文件 `config/Console/Logger.yml`，  
在 `loggers` 下增加一句 `名称: NONE` 即可将该名称的日志等级改为 `无`，即不输出日志。  
举个例子：
```yaml
# 特定日志记录器输出等级
loggers:
  com.github.unidbg.linux.ARM64SyscallHandler: NONE
  com.github.unidbg.linux.AndroidSyscallHandler: NONE
  stderr: NONE
```

# 在未来核心中使用

> 至少需要爪哇 8，不支持安德瑞德。  
> 安德瑞德请参见 [浮球罗假签名](https://github.com/fuqiuluo/SignFaker)

先引用本插件为依赖

```kotlin
// Gradle: build.gradle(.kts)
repositories {
  mavenCentral()
}
dependencies {
  implementation("top.mrxiaom:qsign:$QSIGN_VERSION")
}
```
```xml
<!-- Maven: pom.xml -->
<dependencies>
  <dependency>
    <groupId>top.mrxiaom</groupId>
    <artifactId>qsign</artifactId>
    <version>$QSIGN_VERSION</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

在登录前执行以下代码即可，最好在程序初始化时执行

[咳特灵用法](src/test/kotlin/CoreUsage.kt) | [爪哇用法](src/test/java/CoreUsage.java)

# 编译

编译插件
```
./gradlew buildPlugin
```
打包发布 (将会在项目目录生成 `qsign-x.x.x-all.zip`)
```
./gradlew deploy
```
