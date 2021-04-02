create table messages
(
    id           char(15)                not null,
    sender_id    bigint,
    receiver_id  bigint,
    group_id     bigint,
    kind         smallint                not null,
    content      varchar(4096),
    content_kind smallint  default 1     not null,
    url          varchar(1024),
    custom_args  varchar(2048),
    unread       smallint  default 0,
    create_time  timestamp default now() not null,
    update_time  timestamp default now() not null
);

comment on table messages is '消息表';

comment on column messages.id is '全局消息 ID';

comment on column messages.sender_id is '发送人 ID';

comment on column messages.receiver_id is '接收人 ID';

comment on column messages.group_id is '群 ID';

comment on column messages.kind is '数据类型
1: 系统消息
2: 私聊消息
3: 群聊消息';

comment on column messages.content is '消息内容';

comment on column messages.content_kind is '消息内容类型
0: 普通文本消息
1: 图片消息
2: 音频消息
3: 视频消息
9: 系统消息';

comment on column messages.url is '统一资源定位器';

comment on column messages.custom_args is '自定义参数';

comment on column messages.unread is '消息是否未读
0: 已读
1: 未读';

comment on column messages.create_time is '创建时间';

comment on column messages.update_time is '更新时间';

create unique index messages_id_uindex
    on messages (id);
