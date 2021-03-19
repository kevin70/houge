create table user_messages
(
    uid         bigint      not null,
    message_id  varchar(15) not null,
    create_time timestamp   not null
);

comment on table user_messages is '用户消息关联';

comment on column user_messages.uid is '用户 ID';

comment on column user_messages.message_id is '消息 ID';

comment on column user_messages.create_time is '创建时间';

create index user_messages_message_id_index
    on user_messages (message_id);

create index user_messages_uid_create_time_index
    on user_messages (uid asc, create_time desc);