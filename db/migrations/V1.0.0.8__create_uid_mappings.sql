create table uid_mappings
(
    id          bigserial    not null
        constraint uid_mappings_pk
            primary key,
    mapped_uid  varchar(128) not null,
    create_time timestamp    not null
);

comment on table uid_mappings is '用户 ID 字符串与数值映射表';

comment on column uid_mappings.id is '主键';

comment on column uid_mappings.mapped_uid is '映射的用户 ID';

comment on column uid_mappings.create_time is '创建时间';

create unique index uid_mappings_mapped_uid_uindex
    on uid_mappings (mapped_uid);
