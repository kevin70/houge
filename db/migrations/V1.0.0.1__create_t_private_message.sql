create table t_private_message
(
    id char(14) not null,
    sender_id varchar(128) not null,
    receiver_id varchar(128) not null,
    kind smallint default 1 not null,
    content varchar(4096),
    url varchar(1024),
    custom_args varchar(512),
    unread smallint default 1 not null,
    create_time timestamp default now() not null,
    update_time timestamp default now() not null
);

comment on table t_private_message is '私聊消息';

comment on column t_private_message.id is '全局唯一消息 ID';

comment on column t_private_message.sender_id is '发送人 ID';

comment on column t_private_message.receiver_id is '接收人 ID';

comment on column t_private_message.kind is '消息类型
1：普通文本消息
2：图片消息
3：音频消息
4：视频消息';

comment on column t_private_message.content is '消息内容';

comment on column t_private_message.url is '统一资源定位器
图片URL、视频URL';

comment on column t_private_message.custom_args is '自定义参数';

comment on column t_private_message.unread is '消息是否未读
1：未读
0：已读';

comment on column t_private_message.create_time is '创建时间';

comment on column t_private_message.update_time is '更新时间';

create unique index t_private_message_id_uindex
	on t_private_message (id);

create index t_private_message_receiver_id_index
	on t_private_message (receiver_id);

create index t_private_message_sender_id_index
	on t_private_message (sender_id);