create table t_private_message
(
    uid varchar(128) not null,
    message_id varchar(48) not null,
    sender varchar(128),
    receiver varchar(128),
    kind smallint default 1 not null,
    content varchar(4096),
    url varchar(1024),
    custom_args varchar(512),
    unread smallint default 1 not null,
    seq integer not null,
    create_time timestamp default now() not null,
    update_time timestamp default now() not null
);

comment on table t_private_message is '私聊消息';

comment on column t_private_message.uid is '消息所属的用户 ID';

comment on column t_private_message.message_id is '用户全局唯一消息 ID';

comment on column t_private_message.sender is '发送人 ID，如果值为 null 则与 uid 列的值一致';

comment on column t_private_message.receiver is '接收人 ID，如果值为 null 则与 uid 列的值一致';

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

comment on column t_private_message.seq is '序列';

comment on column t_private_message.create_time is '创建时间';

comment on column t_private_message.update_time is '更新时间';

create unique index t_private_message_uid_message_id_uindex
	on t_private_message (uid, message_id);

create index t_private_message_uid_create_time_index
	on t_private_message (uid asc, create_time desc);