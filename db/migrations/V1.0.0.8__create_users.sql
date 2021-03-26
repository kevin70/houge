create table users
(
    id          bigint    not null
        constraint users_pk
            primary key,
    origin_uid  varchar(128),
    create_time timestamp not null,
    update_time timestamp not null
);

comment on column users.origin_uid is '原用户 ID';

comment on column users.create_time is '创建时间';

comment on column users.update_time is '更新时间';

create unique index users_origin_uid_uindex
    on users (origin_uid);