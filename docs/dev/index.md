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
create
database tethys;
```

获取 Tethys 源码：

```
$ git clone https://gitee.com/kk70/tethys.git
```

### Tethys 目录介绍

```
tethys                    项目根目录
  |- .gitee               存放 gitee 源码托管的配置
  |- .github              存放 github 源码托管的配置
  |- config               Tethys 项目所使用的配置
  |- db                   数据库 Schema 脚本
  |- docs                 Tethys 文档存放目录
  |- tethys-core          Tethys 公共的核心逻辑模块
  |- tethys-r2dbc         R2DBC 公共接口及实现模块
  |- tethys-grpc          Tethys gRPC 生成代码及 proto 文件管理模块
  |- tethys-auth          Tethys 认证逻辑实现模块
  |- tethys-storage       Tethys 数据存储逻辑实现模块
  |- tethys-service       Tethys 服务逻辑实现模块
  |- tethys-server-logic  Tethys 业务逻辑实现 gRPC 服务
  |- tethys-server-rest   Tethys 开放的 HTTP 实现服务
  |- tethys-server-ws     Tethys 开放的 WebSocket 服务
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

**FlywayDB 数据库连接配置：**

- `flyway.url` 数据库连接地址
- `flyway.user` 数据库连接用户
- `flyway.password` 数据库连接密码

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

## 运行服务

在所有基本配置准备工作结束之后，现在我们来启动 Tethys 服务吧。

### IM 服务

![](images/20210413134827.png)

`top.yein.tethys.im.main.ImMain` 是 IM 服务的主程序入口，可直接使用 IDE 运行服务。打印出如下的日志表示 IM 服务已经启动成功。

```
... 省略的日志

13:53:13.179 [main] INFO  top.yein.tethys.im.server.ImServer 90 - IM Server 启动完成 - 0.0.0.0:11010
13:53:13.180 [main] INFO  top.yein.tethys.im.main.ImMain 85 - tethys-im 服务启动成功 fid=93106
13:53:13.353 [main] INFO  top.yein.tethys.im.server.GrpcServer 54 - gRPC 添加服务 top.yein.tethys.grpc.service.MessageGrpcImpl@44114b9f
13:53:13.406 [main] INFO  top.yein.tethys.im.server.GrpcServer 63 - gRPC 服务 0.0.0.0:11012 启动成功
```

### REST 服务

![](images/20210413134901.png)

`top.yein.tethys.rest.main.RestMain` 是 REST 服务的主程序入口，可直接使用 IDE 运行服务。打印出如下的日志表示 REST 服务已经启动成功。

```
... 省略的日志

13:55:54.653 [main] INFO  top.yein.tethys.rest.server.RestServer 77 - REST Server 启动完成 - 0.0.0.0:11019
13:55:54.653 [main] INFO  top.yein.tethys.rest.main.RestMain 79 - tethys-rest 服务启动成功 fid=99494
```

## 创建用户 - *S*

Tethys 自身并没有独立的用户系统，业务需要在使用之前需要将用户 ID 同步到 Tethys 中，在用户同步完成之后才可以收发信息。

**[创建用户 API：](https://kk70.gitee.io/tethys/tethys-rest.html#tag/SUPPORT/paths/~1i~1users/post)**

**Request：**

```
POST /i/users HTTP/1.1
Host: 127.0.0.1:11019
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "id": 1
}
```

使用 `/i/users` 接口同步创建用户。

## 创建群组 - *S*

如果需要使用群组会话公司，业务方也需要将群组同步到 Tethys 中，并绑定群组与用户的关系。

**[创建群组 API：](https://kk70.gitee.io/tethys/tethys-rest.html#tag/SUPPORT/paths/~1i~1groups/post)**

**Request：**

```
POST /i/groups HTTP/1.1
Host: 127.0.0.1:11019
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "id": 100,
  "creator_id": 1,
  "member_limit": 30
}
```

使用 `/i/groups` 接口同步创建群组，其中的 `creator_id` 为创建者的**用户-ID**，`creator_id`的值必须是在 Tethys 系统中已经创建并且存在的用户。

**[群组绑定用户 API：](https://kk70.gitee.io/tethys/tethys-rest.html#tag/SUPPORT/paths/~1i~1groups-member~1{group_id}~1join/put)**

**Request：**

```
PUT /i/group-members/100/join HTTP/1.1
Host: 127.0.0.1:11019
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "uid": 9
}
```

将用户 **9** 与群组 **100** 建立关系，之后给群组 **100** 发送消息时用户 **9** 才可收到该群组的消息。

## 生成令牌 - *S*

要与 Tethys 创建接口通讯，首先需要访问令牌。

**[生成令牌 API：](https://kk70.gitee.io/tethys/tethys-rest.html#tag/SUPPORT/paths/~1i~1token~1{uid}/post)**

**Request：**

```
POST /i/token/3 HTTP/1.1
Host: 127.0.0.1:11019
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

**Response：**

```
{
  "access_token": "eyJraWQiOiJBMCIsInR5cCI6IkpXVCIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiIxIn0.Q4ZLkZ9my5KH-nRUQX3zl6dR01XnMH20Zf52RHjHUSnrxI4J-HgDS1ScTNWo_O6LWYwb5ntEi--APQDaH__0IQ"
}
```

该接口必须由业务服务端调用，获取到 `access_token` 之后响应给终端，终端通过 `access_token` 才可访问 Tethys 对外的接口。

## 收发消息

### WebSocket 收发消息

- 创建链接

  ```
  $ websocat websocat -E ws://127.0.0.1:11010/ws?access_token=[access_token]
  ```

  - **127.0.0.1:11010** 为 Tethys WebSocket 服务的主机与端口
  - **[access_token]** 替换为访问用户的令牌

- 演示

  ![](images/328994a573dc.gif)

### HTTP 发送消息

- 演示

  ![](images/a21fa82f2da7.gif)

### gRPC 发送消息

- 演示

  ![](images/4c04babfe553.gif)

> :warning: 所有以 *S* 结尾的接口，都是 Server to Server 模式，采用 Basic 认证，这些接口应该是在安全可信的环境中调用执行。