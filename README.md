[![CI/CD](https://github.com/kevin70/tethys/workflows/Tethys%20CI/CD/badge.svg)](https://github.com/kevin70/tethys/actions)
[![codecov](https://codecov.io/gh/kevin70/tethys/branch/main/graph/badge.svg?token=BRJECD0HF0)](https://codecov.io/gh/kevin70/tethys)

# Tethys
[We Are Reactive](https://www.reactivemanifesto.org/zh-CN)

致力于打造是一个**免费安全可靠**的企业级 IM 解决方案。

## 目标
- 高性能
- 可靠的

## 功能
- [x] 用户认证
- [x] 私人聊天
- [x] 群组聊天
- [ ] 集群部署
- [ ] 消息存储
- [ ] 容器部署
- [ ] 系统监控
- [ ] 黑名单

## 开发准备
- Java 11
- IntelliJ IDEA
    - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
    - [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)
    - [SonarLint](https://www.sonarlint.org/intellij)
- Redis

### Google Java Format 配置
1. 去到 `File → Settings → Editor → Code Style`
2. 单击带有工具提示的扳手图标显示计划动作
3. 点击 `Import Scheme`
4. 选择项目根目录下 `config/intellij-java-google-style.xml` 文件
5. 确保选择 GoogleStyle 作为当前方案

## 文档
- [用户认证](docs/design/authentication.md)
- [消息协议](docs/design/message_protocol.md)

## Browser WebSocket Client
使用 Chrome 浏览器安装扩展 [Browser-WebSocket-Client
](https://github.com/abeade/browser-websocket-client) 用于快速测试 WebSocket。


## 感谢
Tethys 的实现离不开源社区的支持，感恩为开源做出贡献的人。

## 捐赠
如果您觉得 Tethys 做得不错，对您有实际的帮助，请支持我们更好的维护项目。

![Alipay](docs/images/alipay_qrcode.png)
![Wechat](docs/images/wechat_qrcode.png)
