create table server_instances
(
    id              integer           not null
        constraint server_instances_pk
            primary key,
    app_name        varchar(128)      not null,
    host_name       varchar(64)       not null,
    host_address    varchar(64)       not null,
    os_name         varchar(64),
    os_version      varchar(64)       not null,
    os_arch         varchar(64)       not null,
    os_user         varchar(64),
    java_vm_name    varchar(64)       not null,
    java_vm_version varchar(32)       not null,
    java_vm_vendor  varchar(64)       not null,
    work_dir        varchar(512)      not null,
    pid             bigint            not null,
    ver             integer default 1 not null,
    create_time     timestamp         not null,
    check_time      timestamp         not null
);

comment on table server_instances is '服务实例信息';

comment on column server_instances.id is '主键';

comment on column server_instances.app_name is '应用名称';

comment on column server_instances.host_name is '主机名称';

comment on column server_instances.host_address is '主机 IP 地址';

comment on column server_instances.os_name is '系统名称';

comment on column server_instances.os_version is '系统版本';

comment on column server_instances.os_arch is 'OS Arch';

comment on column server_instances.os_user is '系统的用户名';

comment on column server_instances.java_vm_name is 'Java 虚拟机名称';

comment on column server_instances.java_vm_version is 'Java 虚拟机版本';

comment on column server_instances.java_vm_vendor is 'Java 虚拟机供应商';

comment on column server_instances.work_dir is '服务的工作目录';

comment on column server_instances.pid is '进程 ID';

comment on column server_instances.ver is '数据版本';

comment on column server_instances.create_time is '创建时间';

comment on column server_instances.check_time is '最后检查时间，与当前时间相差超过1小时则默认服务已销毁';