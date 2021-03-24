create table ent_clients
(
    id            serial            not null
        constraint ent_clients_pk
            primary key,
    client_id     varchar(128)      not null,
    client_secret varchar(512)      not null,
    deleted       integer default 0 not null,
    create_time   timestamp         not null,
    update_time   timestamp         not null
);

comment on table ent_clients is '企业客户对接配置';

comment on column ent_clients.id is '企业客户 ID';

comment on column ent_clients.client_id is 'API 对接的 ID';

comment on column ent_clients.client_secret is 'API 对接的密钥';

comment on column ent_clients.deleted is '软删除标志（0：数据正常可用，非0代表数据不可用，一般将标志设置为删除的时间戳 Unix Timestamp）';

comment on column ent_clients.create_time is '创建时间';

comment on column ent_clients.update_time is '更新时间';

create unique index ent_clients_client_id_uindex
    on ent_clients (client_id);