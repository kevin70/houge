create table t_server_instance
(
    id          integer      not null
        constraint t_server_instance_pk
            primary key,
    host_name   varchar(128) not null,
    host_ip     varchar(64)  not null,
    system_name varchar(128) not null,
    system_user varchar(128) not null,
    pid         integer      not null,
    work_dir    varchar(512) not null,
    create_time timestamp    not null,
    check_time  timestamp    not null
);

comment on table t_server_instance is 'IM 服务实例信息';

comment on column t_server_instance.id is '主键';

comment on column t_server_instance.host_name is '运行服务的主机名称';

comment on column t_server_instance.host_ip is '运行服务的主机 IP';

comment on column t_server_instance.system_name is '运行服务的系统名称';

comment on column t_server_instance.system_user is '运行服务的系统登录用户';

comment on column t_server_instance.pid is '应用实例的进程 ID';

comment on column t_server_instance.work_dir is '服务运行的工作目录';

comment on column t_server_instance.create_time is '创建时间（服务启动时间）';

comment on column t_server_instance.check_time is '最新检查时间，与当前时间相差1小时，视该服务实例已经销毁';
