create table t_jwt_secret
(
    id char(2) not null
        constraint t_jwt_secrets_pk
            primary key,
    algorithm varchar(10) not null,
    secret_key bytea not null,
    deleted integer default 0 not null,
    create_time timestamp default now() not null,
    update_time timestamp default now() not null
);

comment on table t_jwt_secret is 'JWT 密钥';

comment on column t_jwt_secret.algorithm is 'JWT 签名算法名称
当前支持 HMAC 家族的加密算法';

comment on column t_jwt_secret.id is 'kid 标识仅支持2个字符';

comment on column t_jwt_secret.secret_key is '密钥';

comment on column t_jwt_secret.deleted is '删除数据的时间戳（秒），值不为 0 时，表示该行数据已被软删除';

comment on column t_jwt_secret.create_time is '创建时间';

comment on column t_jwt_secret.update_time is '修改时间';
