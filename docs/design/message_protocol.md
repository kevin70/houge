# 消息协议

## 聊天消息

```json
{
  "@ns": "message",
  "message_id": "7KBCAXS2QCKQJHX",
  "kind": 0,
  "from": 1,
  "to": 2,
  "content": "消息内容~~~",
  "content_type": 0,
  "extra_args": "扩展参数~~~"
}
```

| 属性名称 | 数据类型 | 必选 | 描述 |
| --- | --- | --- | --- |
| `@ns` | `string` | Y | 命名空间，固定取值 `message` |
| `message_id` | `string(15)` | Y | 消息 ID 全局唯一 |
| `from` | `int64` | N | 发送者**用户-ID** |
| `to` | `int64` |Y | 接收者**用户-ID**、**群组-ID** |
| `kind` | `int32` | N | 消息类型，默认值 `0`。 |
| `content` | `string(4096)` | Y | 消息内容 |
| `content_type` | `int32` | N | 消息内容类型，默认值 `0`。 |
| `extra_args` | `string(2048)` | N | 扩展参数 |

**消息枚举定义:**
- `kind` 消息类型枚举：
  - `0` 私人消息
  - `1` 群组消息
- `content_type` 消息内容类型枚举：
  - `0` 文本消息
  - `1` 图片消息
  - `2` 语音消息
  - `3` 视频消息

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
