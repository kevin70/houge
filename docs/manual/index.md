使用手册 <!-- omit in toc -->
===

- [安装部署](#安装部署)
  - [先决条件](#先决条件)
  - [安装 PostgreSQL](#安装-postgresql)
  - [安装 IM 服务](#安装-im-服务)
  - [安装 REST 服务](#安装-rest-服务)
- [IM 聊天测试](#im-聊天测试)

## 安装部署

安装说明以 [Docker](https://docs.docker.com) 为基础承载所有 Houge 服务运行。

### 先决条件

- [Docker 安装](https://docs.docker.com/get-docker)
- [Docker Compose 安装](https://docs.docker.com/compose/install)

在开始安装服务之前请先按照下面的方法确认基础依赖已经成功安装。

> 使用 `docker --version` 命令查看 Docker 的版本信息确认 Docker 已经成功安装。
> ```
> $ docker --version
> ```
> Docker version 20.10.2, build 2291f61

> 使用 `docker-compose --version` 命令查看 Docker Compose 的版本信息确认 Docker Compose 已经成功安装。
> ```
> $ docker-compose --version
> ```
> docker-compose version 1.27.4, build 40524192

### 安装 PostgreSQL

Houge 开发数据库采用的是 [PostgreSQL](https://www.postgresql.org/) ，推荐使用 PostgreSQL 13 及以上的版本。

```
$ docker run -d --name postgres -p 5432:5432 \
    -e POSTGRES_USER=houge -e POSTGRES_PASSWORD=123456 \
    postgres:13
```

这里采用的镜像是 [postgres:13](https://hub.docker.com/_/postgres) 。

> `POSTGRES_USER` 这里设置的用户名为 `houge` 会创建一个默认数据库 `houge`。
>
> 如果你未使用上面的方法安装 PostgreSQL 或用户名不为 `houge` 时，你需要手动在 PostgreSQL 中创建数据库 `houge`。
> ```
> create database houge;
> ```

到这里 PostgreSQL 数据库已经安装完成。

---

接下来我们需要安装 Houge 数据库结构信息。 我们采用 [Flyway](https://flywaydb.org/) 管理数据库结构，并且已发布了 Docker 镜像 `houge-db-migration`
，通过下面的方式可以帮助你快速安装。

```
$ docker run --rm \
    -e FLYWAY_URL=jdbc:postgresql://[POSTGRES_HOST]:[POSTGRES_PORT]/houge \
    -e FLYWAY_USER=[POSTGRES_USER]
    -e FLYWAY_PASSWORD=[POSTGRES_PASSWORD] \
    kevin70/houge-db-migration migrate
```

在运行命令之前需要将其中的变量替换为真实场景的数据。

- `POSTGRES_HOST` PostgreSQL 数据库的访问主机 IP；
- `POSTGRES_PORT` PostgreSQL 数据库的访问端口（默认为：5432）；
- `POSTGRES_USER` PostgreSQL 数据库的连接用户名；
- `POSTGRES_PASSWORD` PostgreSQL 数据库的连接用户密码。

---

```
Flyway Community Edition 7.5.3 by Redgate
Database: jdbc:postgresql://db/houge (PostgreSQL 13.1)
Successfully validated 5 migrations (execution time 00:00.012s)
Creating Schema History table "public"."flyway_schema_history" ...
......
...... 省略的日志
......
Successfully applied 5 migrations to schema "public" (execution time 00:00.122s)
```

在控制台中打印的日志类似如上信息时数据库结构已经安装完成。

### 安装 IM 服务

IM 镜像名称是 `kevin70/houge-im`，通过下面的命令可快速安装启动 IM 服务。

```
$ docker run -d --name houge-im \
    -e HOUGE_MESSAGE_STORAGE_R2DBC_URL=[R2DBC_URL] \
    -p 11010:11010 \
    kevin70/houge-im
```

- `R2DBC_URL` PostgreSQL 数据库连接 URL。

> **R2DBC URL 格式：**
> ```
>  r2dbc:a-driver:pipes://localhost:3306/my_database?locale=en_US
>  \___/ \______/ \___/   \____________/\__________/\___________/
>    |       |      |           |           |           |
>  scheme  driver  protocol  authority    path        query
> ```
>
> **配置示例：**
> ```
> r2dbc:postgresql://localhost:123456@postgres:5432/houge
> ```

```
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar
......
...... 省略的日志
......
05:21:20.843 [main] INFO  top.yein.houge.im.server.ImServer 75 - IM Server 启动完成 - 0.0.0.0:11010
05:21:20.844 [main] INFO  top.yein.houge.im.main.ImMain 36 - houge-im 服务启动成功 fid=108932
```

在控制台中打印的日志类似如上信息时 IM 服务已经安装完成。

### 安装 REST 服务

REST 镜像名称是 `kevin70/houge-REST`，通过下面的命令可快速安装启动 REST 服务。

```
$ docker run -d --name houge-im \
    -e HOUGE_MESSAGE_STORAGE_R2DBC_URL=[R2DBC_URL] \
    -p 11019:11019 \
    kevin70/houge-im
```

- `R2DBC_URL` PostgreSQL 数据库连接 URL。

```
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar
......
...... 省略的日志
......
05:21:23.955 [main] INFO  top.yein.houge.rest.server.RestServer 53 - REST Server 启动完成 - 0.0.0.0:11019
05:21:23.956 [main] INFO  top.yein.houge.rest.main.RestMain 36 - houge-rest 服务启动成功 fid=30931
```

在控制台中打印的日志类似如上信息时 REST 服务已经安装完成。

## IM 聊天测试



