# 开发手册

## 前期准备

Tethys 是采用 reactor 基于 AdoptOpenJDK 11 的版本开发，消息存储默认采用 PostgreSQL 13 的版本。这里仅提供需要安装的软件的版本、工具的名称，不提供详细的安装步骤。

1. 安装 [AdoptOpenJDK 11](https://adoptopenjdk.net/) Tethys 开发默认使用的发行版，建议统一使用 AdoptOpenJDK；
2. 安装 [PostgreSQL 13](https://www.postgresql.org/) 用于消息存储；
3. 安装 [Intellij IDEA Community](https://www.jetbrains.com/idea/download) Java 开发工具；
4. 安装 [Firecamp](https://firecamp.io/) 用于 WebSocket 接口测试；
5. 安装 [BloomRPC](https://github.com/uw-labs/bloomrpc) 用于 gRPC 接口测试；
6. 安装 [Postman](https://www.postman.com/) 用于 HTTP 接口测试，Firecamp 也可以用于测试 HTTP 接口这主要看个人习惯选择。

开发、测试工具，这里是个人平时习惯使用的工具，提供给大家的一个参考选项，具体可根据个人习惯选择性使用。

开发工具安装配置结束后需要手动在 PostgreSQL 中创建数据库，可使用下面的 SQL 快速创建数据库。

```sql
create database tethys;
```

获取 Tethys 源码

```
$ git clone https://gitee.com/kk70/tethys.git
```

## 开发配置

### FlywayDB 配置

Tethys 默认采用 [FlywayDB](https://flywaydb.org/) 管理数据库 Schema 脚本，默认已经与 Gradle 集成，我们仅需要配置一次 flyway 数据库连接信息。 在 tethys
项目根目录中有一个 `flyway.conf.template` 文件。我们将文件复制并命名为 `flyway.conf` 放置在 tethys 根目录中（参考下图）。

![](images/20210412184533.png)

文件复制成功之后需要将文件内的数据库连接配置修改为自己搭建的数据库连接配置（参考下图标注）。

- `flyway.url` PostgreSQL 数据库连接地址
- `flyway.user` PostgreSQL 数据库连接用户
- `flyway.password` PostgreSQL 数据库连接密码

![](images/20210412184905.png)

在之后的开发过程中在数据库环境不变的情况下无需修改 `flyway.conf` 配置文件，该文件也不需要提交到 VCS 中管理，每个开发人员都会有单独的一份，避免开发中因环境不一致的原因造成开发冲突（并且在 `.gitignore`
文件中默认已经将该文件忽略不会提交至 VCS）。

如果有公共的配置项请直接修改 `flyway.conf.template` 配置文件，在修改后所有开发人员需要手动同步更新至 `flyway.conf` 文件中。

在 Gradle 中默认已经集成了 flyway 插件，后续所有数据库结构更新都可直接如下命令操作：

```shell
$ ./gradlew :flywayMigrate
```
