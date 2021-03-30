[![CI/CD](https://github.com/kevin70/tethys/workflows/Tethys%20CI/CD/badge.svg)](https://github.com/kevin70/tethys/actions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kevin70_tethys&metric=coverage)](https://sonarcloud.io/dashboard?id=kevin70_tethys)
[![Tethys IM](https://pub.idqqimg.com/wpa/images/group.png)](https://qm.qq.com/cgi-bin/qm/qr?k=W8UiTh5rmq4O0SZJFnnWfh3SegzTGIWo&jump_from=webapi)

# Tethys

[We Are Reactive](https://www.reactivemanifesto.org/zh-CN)

一个 IM 服务端项目，采用 [Spring Reactor](https://projectreactor.io/) 作为基础技术研发，全站响应式技术应用(Reactor/R2DBC/Netty)。

内置完整的 IM 通讯协议，使用 WebSocket + HTTP 方式实现整个 IM 系统相关业务，完全独立运行并且能很好的与现有的业务系统整合。

**你可用于它快速搭建私域 IM 服务，或者用于 Java 响应式技术研究项目。**

## 特点

- 响应式
- 免费的
- 高性能
- 可靠的

## 流程图

![](docs/images/flow-20200330.png)

## 功能

- [x] 用户认证
- [x] 私人聊天
- [x] 群组聊天
- [x] 消息存储
- [x] 容器部署
- [x] 好友关系
- [x] 离线消息
- [ ] 集群部署
- [ ] 系统监控
- [ ] 黑名单

## 开发准备

- Java 11
- IntelliJ IDEA
    - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
    - [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)
    - [SonarLint](https://www.sonarlint.org/intellij)
- Redis
- [websocat](https://github.com/vi/websocat) WebSocket 命令行测试工具

### Google Java Format 配置

1. 去到 `File → Settings → Editor → Code Style`
2. 单击带有工具提示的扳手图标显示计划动作
3. 点击 `Import Scheme`
4. 选择项目根目录下 `config/intellij-java-google-style.xml` 文件
5. 确保选择 GoogleStyle 作为当前方案

## 文档

- [使用手册](docs/manual/index.md)
- [用户认证](docs/design/authentication.md)
- [消息协议](docs/design/message_protocol.md)
- [安装部署](docs/deployment/install.md)
- [分布式消息 ID 设计](docs/design/message_id.md)
- [REST OpenAPI 接口文档](https://kk70.gitee.io/tethys/tethys-rest-oais/tethys-rest.html)

## Browser WebSocket Client

使用 Chrome 浏览器安装扩展 [Browser-WebSocket-Client
](https://github.com/abeade/browser-websocket-client) 用于快速测试 WebSocket。

## 演示

![](docs/images/tethys-im-demo1.gif)
[Tethys IM 演示视频1](https://www.bilibili.com/video/BV1CN411Q7dX)

## 私聊演示

![](docs/images/p-msg-44.gif)
![](docs/images/p-msg-55.gif)

## 群聊演示

![](docs/images/g-msg-44.gif)
![](docs/images/g-msg-55.gif)
![](docs/images/g-msg-66.gif)

## 感谢

Tethys 的实现离不开源社区的支持，感恩为开源做出贡献的人。

## 捐赠

如果您觉得 Tethys 做得不错，对您有实际的帮助，请支持我们更好的维护项目。

![Alipay](docs/images/alipay_qrcode.png)
![Wechat](docs/images/wechat_qrcode.png)
