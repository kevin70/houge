# 消息协议

## 消息枚举定义

- `content_type` 消息内容类型枚举：
  - `0` 文本消息
  - `1` 图片消息
  - `2` 语音消息
  - `3` 视频消息

## 私人消息

```json
{
  "@ns": "p.message",
  "message_id": "7KBCAXS2QCKQJHX",
  "from": 1,
  "to": 2,
  "content": "消息内容~~~",
  "content_type": 0,
  "extra_args": "扩展参数~~~"
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `p.message` |
| `message_id` | `string(15)` | Y | 消息 ID 全局唯一 |
| `from` | `int64` | N | 发送者**用户-ID** |
| `to` | `int64` |Y | 接收者**用户-ID** |
| `content` | `string(4096)` | Y | 消息内容 |
| `content_type` | `int32` | N | 消息内容类型，默认值 `0`。 |
| `extra_args` | `string(2048)` | N | 扩展参数 |

## 群组消息

```json
{
  "@ns": "g.message",
  "message_id": "7KBCAXS2QCKQJHX",
  "from": 1,
  "to": 2,
  "content": "消息内容~~~",
  "content_type": 0,
  "extra_args": "扩展参数~~~"
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `g.message` |
| `message_id` | `string(15)` | Y | 消息 ID 全局唯一 |
| `from` | `int64` | N | 发送者**用户-ID** |
| `to` | `int64` |Y | 接收者**群组-ID** |
| `content` | `string(4096)` | Y | 消息内容 |
| `content_type` | `int32` | N | 消息内容类型，默认值 `0`。 |
| `extra_args` | `string(2048)` | N | 扩展参数 |

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
