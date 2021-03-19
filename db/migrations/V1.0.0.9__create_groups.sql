create sequence groups_id_seq;

comment on sequence groups_id_seq is '群 ID 生成序列';

create table groups
(
    id          bigint not null
        constraint groups_pk
            primary key,
    name        varchar(18),
    creator_id  bigint    not null,
    owner_id    bigint,
    member_size integer   not null,
    member_limit integer   not null,
    create_time timestamp not null,
    update_time timestamp not null
);

comment on table groups is '群信息';

comment on column groups.id is '群 ID';

comment on column groups.name is '群名称';

comment on column groups.creator_id is '创建者用户 ID';

comment on column groups.owner_id is '拥有者用户 ID';

comment on column groups.member_size is '群成员数量';

comment on column groups.member_limit is '群成员数量限制';

comment on column groups.create_time is '创建时间';

comment on column groups.update_time is '更新时间';
