# xim
IM 服务器

## 开发准备
- Java 11
- IntelliJ IDEA
    - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
    - [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)
- Redis

### Google Java Format 配置
1. 去到 `File → Settings → Editor → Code Style`
2. 单击带有工具提示的扳手图标显示计划动作
3. 点击 `Import Scheme`
4. 选择项目根目录下 `config/intellij-java-google-style.xml` 文件
5. 确保选择 GoogleStyle 作为当前方案

## 消息协议
### 文本消息
```json
{
  "ns": "private.msg",
  "from": "from user id",
  "to": "to user id",
  "content": "Hello World!"
}
```

---

| 属性名称 | 描述 |
| --- | --- |
| `ns`| 命名空间，固定取值 `private.msg` |
| `from` | 发送者用户 ID |
| `to` | 接收者用户 ID |
| `content` | 消息内容 |

### 订阅群组消息
### 取消订阅群组消息

### 群聊