create table t_group_message
(
    id varchar(48) not null,
    gid bigint not null,
    sender_id varchar(128) not null,
    kind smallint default 1 not null,
    content varchar(4096),
    url varchar(1024),
    custom_args varchar(512),
    create_time timestamp default now() not null,
    update_time timestamp default now() not null
);

comment on table t_group_message is '群组消息';

comment on column t_group_message.id is '全局唯一消息 ID';

comment on column t_group_message.gid is '群组 ID';

comment on column t_group_message.sender_id is '发送人 ID';

comment on column t_group_message.kind is '消息类型
1：普通文本消息
2：图片消息
3：音频消息
4：视频消息';

comment on column t_group_message.content is '消息内容';

comment on column t_group_message.url is '统一资源定位器
图片URL、视频URL';

comment on column t_group_message.custom_args is '自定义参数';

comment on column t_group_message.create_time is '创建时间';

comment on column t_group_message.update_time is '修改时间';

create unique index t_group_message_id_uindex
	on t_group_message (id);

create index t_group_message_gid_create_time_index
	on t_group_message (gid asc, create_time desc);