# 消息协议

## 私聊
### 文本消息
```json
{
  "@ns": "private.msg",
  "msg_id": "643bf3b250c211ebae930242ac130002",
  "from": "from user id",
  "to": "to user id",
  "kind": 1,
  "content": "Hello World!",
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `private.msg` |
| `msg_id` | `string` | Y | 消息 ID 全局唯一 |
| `from` | `string` | Y | 发送者用户 ID |
| `to` | `string` |Y | 接收者用户 ID |
| `kind` | `int32` | Y | 内容类型，固定取值 `1` |
| `content` | `string` | Y | 消息内容 |
| `custom_args` | `string` | N | 扩展参数 |

### 图片消息
```json
{
  "@ns": "private.msg",
  "msg_id": "643bf3b250c211ebae930242ac130002",
  "from": "from user id",
  "to": "to user id",
  "kind": 2,
  "url": "https://via.placeholder.com/150",
  "content": "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWBAMAAADOL2zRAAAAG1BMVEXMzMyWlpaqqqq3t7fFxcW+vr6xsbGjo6OcnJyLKnDGAAAACXBIWXMAAA7EAAAOxAGVKw4bAAABAElEQVRoge3SMW+DMBiE4YsxJqMJtHOTITPeOsLQnaodGImEUMZEkZhRUqn92f0MaTubtfeMh/QGHANEREREREREREREtIJJ0xbH299kp8l8FaGtLdTQ19HjofxZlJ0m1+eBKZcikd9PWtXC5DoDotRO04B9YOvFIXmXLy2jEbiqE6Df7DTleA5socLqvEFVxtJyrpZFWz/pHM2CVte0lS8g2eDe6prOyqPglhzROL+Xye4tmT4WvRcQ2/m81p+/rdguOi8Hc5L/8Qk4vhZzy08DduGt9eVQyP2qoTM1zi0/uf4hvBWf5c77e69Gf798y08L7j0RERERERERERH9P99ZpSVRivB/rgAAAABJRU5ErkJggg=="
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `private.msg` |
| `msg_id` | `string` | Y | 消息 ID 全局唯一 |
| `from` | `string` | Y | 发送者用户 ID |
| `to` | `string` | Y | 接收者用户 ID |
| `kind` | `int32` | Y | 内容类型，固定取值 `2` |
| `url` | `string` | Y | 图片 URL |
| `content` | `string` | Y | 图片缩略图 **BASE64** 编码 |
| `custom_args` | `string` | N | 扩展参数 |

## 群组
### 订阅群组消息
```json
{
  "@ns": "group.sub",
  "group_ids": ["group id-1", "group id-2"]
}
```

| 属性名称 | 数据类型 | 描述 |
| --- | --- | --- |
| `@ns` | `string` | 命名空间，固定取值 `group.sub` |
| `group_ids` | `array[string]` | 群组 ID 列表 |

### 取消订阅群组消息
```json
{
  "@ns": "group.u@nsub",
  "group_ids": ["group id-1", "group id-2"]
}
```

| 属性名称 | 数据类型 | 描述 |
| --- | --- | --- |
| `@ns` | `string` | 命名空间，固定取值 `group.u@nsub` |
| `group_ids` | `array[string]` | 群组 ID 列表 |

### 文本消息
```json
{
  "@ns": "group.msg",
  "msg_id": "643bf3b250c211ebae930242ac130002",
  "from": "from user id",
  "to": "to group id",
  "kind": 1,
  "content": "Hello World!",
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `group.msg` |
| `msg_id` | `string` | Y | 消息 ID 全局唯一 |
| `from` | `string` | Y | 发送者用户 ID |
| `to` | `string` |Y | 接收者用户 ID |
| `kind` | `int32` | Y | 内容类型，固定取值 `1` |
| `content` | `string` | Y | 消息内容 |
| `custom_args` | `string` | N | 扩展参数 |

### 图片消息
```json
{
  "@ns": "group.msg",
  "msg_id": "643bf3b250c211ebae930242ac130002",
  "from": "from user id",
  "to": "to group id",
  "kind": 2,
  "url": "https://via.placeholder.com/150",
  "content": "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWBAMAAADOL2zRAAAAG1BMVEXMzMyWlpaqqqq3t7fFxcW+vr6xsbGjo6OcnJyLKnDGAAAACXBIWXMAAA7EAAAOxAGVKw4bAAABAElEQVRoge3SMW+DMBiE4YsxJqMJtHOTITPeOsLQnaodGImEUMZEkZhRUqn92f0MaTubtfeMh/QGHANEREREREREREREtIJJ0xbH299kp8l8FaGtLdTQ19HjofxZlJ0m1+eBKZcikd9PWtXC5DoDotRO04B9YOvFIXmXLy2jEbiqE6Df7DTleA5socLqvEFVxtJyrpZFWz/pHM2CVte0lS8g2eDe6prOyqPglhzROL+Xye4tmT4WvRcQ2/m81p+/rdguOi8Hc5L/8Qk4vhZzy08DduGt9eVQyP2qoTM1zi0/uf4hvBWf5c77e69Gf798y08L7j0RERERERERERH9P99ZpSVRivB/rgAAAABJRU5ErkJggg=="
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `group.msg` |
| `msg_id` | `string` | Y | 消息 ID 全局唯一 |
| `from` | `string` | Y | 发送者用户 ID |
| `to` | `string` | Y | 接收者用户 ID |
| `kind` | `int32` | Y | 内容类型，固定取值 `2` |
| `url` | `string` | Y | 图片 URL |
| `content` | `string` | Y | 图片缩略图 **BASE64** 编码 |
| `custom_args` | `string` | N | 扩展参数 |

## 错误响应
```json
{
  "@ns": "error",
  "code": 500,
  "message": "错误描述",
  "details": "any"
}
```
| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `error` |
| `code` | `int32` | Y | 业务错误码 |
| `message` | `string` | Y | 错误描述 |
| `details` | `any` | N | 详细错误描述，复合数据类型 |
