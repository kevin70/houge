# Xim
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

## 感谢
Xim 的实现离不开源社区的支持，感恩为开源做出贡献的人。
