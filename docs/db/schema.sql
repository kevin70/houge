create sequence public.global_id
    minvalue 100000
    increment by 100000
    maxvalue 9223372036854775807
    cycle;

comment on sequence public.global_id is '全局 ID';

create table private_message
(
    id          varchar(48)           not null,
    uid         varchar(48)           not null,
    from_id     varchar(48),
    to_id       varchar(48)           not null,
    kind        smallint,
    content     varchar(4096),
    image_url   varchar(2048),
    ext_args    varchar(4096),
    read        boolean default false not null,
    create_time timestamp             not null,
    update_time timestamp
);

comment on table private_message is '用户私聊消息';

comment on column private_message.id is '消息 ID 全局唯一';

comment on column private_message.uid is '消息的用户';

comment on column private_message.from_id is '消息发送者, 为 null 时取 uid 列的值';

comment on column private_message.to_id is '消息接收者';

comment on column private_message.kind is '消息类型

1: 文本消息
2: 图片消息
3: 语音消息';

comment on column private_message.content is '消息内容';

comment on column private_message.image_url is '图片的 URL';

comment on column private_message.ext_args is '扩展参数';

comment on column private_message.read is '是否已阅读
1: 已读
2: 未读';

comment on column private_message.create_time is ' 创建时间';

comment on column private_message.update_time is '修改时间';

alter table private_message
    owner to postgres;

create unique index private_message_id_uindex
    on private_message (id);



create table group_message
(
    id          varchar(48) not null,
    group_id    varchar(48) not null,
    from_id     varchar(48) not null,
    kind        smallint,
    content     varchar(4096),
    image_url   varchar(2048),
    ext_args    varchar(4096),
    create_time timestamp   not null
);

comment on table group_message is '群组消息';

comment on column group_message.id is '消息 ID';

comment on column group_message.group_id is '群组 ID';

comment on column group_message.from_id is '发送者';

comment on column group_message.kind is '消息类型

1: 文本消息
2: 图片消息
3: 语音消息';

comment on column group_message.content is '消息内容';

comment on column group_message.image_url is '图片 URL';

comment on column group_message.ext_args is '扩展参数';

comment on column group_message.create_time is '创建时间';

alter table group_message
    owner to postgres;

create unique index group_message_id_uindex
    on group_message (id);