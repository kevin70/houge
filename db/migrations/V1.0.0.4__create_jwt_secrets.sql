create table jwt_secrets
(
    id          varchar(8)              not null
        constraint jwt_secrets_pk
            primary key,
    algorithm   varchar(16)             not null,
    secret_key  bytea                   not null,
    deleted     integer   default 0     not null,
    create_time timestamp default now() not null,
    update_time timestamp default now() not null
);

comment on table jwt_secrets is 'JWT 密钥';

comment on column jwt_secrets.id is 'kid 标识仅支持2个字符';

comment on column jwt_secrets.algorithm is 'JWT 签名算法名称
当前支持 HMAC 家族的加密算法';

comment on column jwt_secrets.secret_key is '密钥';

comment on column jwt_secrets.deleted is '删除数据的时间戳（秒），值不为 0 时，表示该行数据已被软删除';

comment on column jwt_secrets.create_time is '创建时间';

comment on column jwt_secrets.update_time is '修改时间';