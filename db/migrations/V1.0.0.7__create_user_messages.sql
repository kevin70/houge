create table user_messages
(
    uid         bigint      not null,
    message_id  varchar(15) not null
);

comment on table user_messages is '用户消息关联';

comment on column user_messages.uid is '用户 ID';

comment on column user_messages.message_id is '消息 ID';

create index user_messages_message_id_index
    on user_messages (message_id);