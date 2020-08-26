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
  "kind": 1,
  "content": "Hello World!",
}
```

| 属性名称 | 数据类型 | 描述 |
| --- | --- | --- |
| `ns` | `string` | 命名空间，固定取值 `private.msg` |
| `from` | `string` | 发送者用户 ID |
| `to` | `string` |接收者用户 ID |
| `kind` | `int32` | 内容类型，固定取值 `1` |
| `content` | `string` | 消息内容 |
| `extra_args` | `string` | 扩展参数 |

### 图片消息
```json
{
  "ns": "private.msg",
  "from": "from user id",
  "to": "to user id",
  "kind": 2,
  "img_src": "https://via.placeholder.com/150",
  "content": "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWBAMAAADOL2zRAAAAG1BMVEXMzMyWlpaqqqq3t7fFxcW+vr6xsbGjo6OcnJyLKnDGAAAACXBIWXMAAA7EAAAOxAGVKw4bAAABAElEQVRoge3SMW+DMBiE4YsxJqMJtHOTITPeOsLQnaodGImEUMZEkZhRUqn92f0MaTubtfeMh/QGHANEREREREREREREtIJJ0xbH299kp8l8FaGtLdTQ19HjofxZlJ0m1+eBKZcikd9PWtXC5DoDotRO04B9YOvFIXmXLy2jEbiqE6Df7DTleA5socLqvEFVxtJyrpZFWz/pHM2CVte0lS8g2eDe6prOyqPglhzROL+Xye4tmT4WvRcQ2/m81p+/rdguOi8Hc5L/8Qk4vhZzy08DduGt9eVQyP2qoTM1zi0/uf4hvBWf5c77e69Gf798y08L7j0RERERERERERH9P99ZpSVRivB/rgAAAABJRU5ErkJggg=="
}
```

| 属性名称 | 数据类型 | 描述 |
| --- | --- | --- |
| `ns` | `string` | 命名空间，固定取值 `private.msg` |
| `from` | `string` | 发送者用户 ID |
| `to` | `string` |接收者用户 ID |
| `kind` | `int32` | 内容类型，固定取值 `2` |
| `img_src` | `string` | 图片 URL |
| `content` | `string` | 图片缩略图 **BASE64** 编码 |
| `extra_args` | `string` | 扩展参数 |

### 订阅群组消息
### 取消订阅群组消息

### 群聊