create table groups_member
(
    gid         bigint    not null,
    uid         bigint    not null,
    create_time timestamp not null
);

comment on table groups_member is '群成员';

comment on column groups_member.gid is '群 ID';

comment on column groups_member.uid is '用户 ID';

comment on column groups_member.create_time is '创建时间';

create unique index groups_member_gid_uid_uindex
    on groups_member (gid, uid);