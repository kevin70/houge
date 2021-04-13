# 开发手册

## 开发准备

Tethys 是采用 reactor 基于 AdoptOpenJDK 11 的版本开发，消息存储默认采用 PostgreSQL 13 的版本。这里仅提供需要安装的软件的版本、名称与链接详细的安装步骤请参考各个官网的安装资料。

- [AdoptOpenJDK 11](https://adoptopenjdk.net/) Tethys 开发默认使用的 JDK 版本
- [PostgreSQL 13](https://www.postgresql.org/) 消息存储数据库
- [Firecamp](https://firecamp.io/) WebSocket GUI 测试工具
- [websocat](https://github.com/vi/websocat) WebSocket 命令行工具
- [BloomRPC](https://github.com/uw-labs/bloomrpc) gRPC GUI 测试工具
- [Postman](https://www.postman.com/) HTTP GUI 接口测试工具
- IntelliJ IDEA
  - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
  - [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)
  - [SonarLint](https://www.sonarlint.org/intellij)

开发、测试工具，这里是个人平时习惯使用的工具，提供给大家的一个参考选项，具体可根据个人习惯选择性使用。

开发工具安装配置结束后需要手动在 PostgreSQL 中创建数据库，可使用下面的 SQL 快速创建数据库。

```sql
create database tethys;
```

获取 Tethys 源码：
```
$ git clone https://gitee.com/kk70/tethys.git
```

### Google Java Format 配置

1. 去到 `File → Settings → Editor → Code Style`
2. 单击带有工具提示的扳手图标显示计划动作
3. 点击 `Import Scheme`
4. 选择项目根目录下 `config/intellij-java-google-style.xml` 文件
5. 确保选择 GoogleStyle 作为当前方案

![](images/20210413093243.png)

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

执行成功通过数据库工具可查看到数据表结构：

![](images/20210413094314.png)

### Tethys 项目配置

Tethys 项目采用 [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) 格式文件作为项目配置文件格式。

> :information_source: [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) 全称 Human-Optimized Config Object Notation（人性化配置对象表示法）是一种人类可读的数据格式，并是 [JSON](https://www.json.org/json-zh.html) 和 [.properties](https://zh.wikipedia.org/wiki/.properties) 的一个超集。

在 tethys 项目**根目录**中手动创建 `tethys.conf` 配置文件。

![](images/20210413132434.png)

**Tethys 配置**

- `message-storage` 消息存储配置
  - `r2dbc.url` 存储数据库连接配置

将 `message-storage.r2dbc.url` 配置为正确的 PostgreSQL 数据库连接信息。

> :warning: 当前配置 r2dbc 连接时必须要将数据库用户名、密码及主机端口信息完整配置在一起。