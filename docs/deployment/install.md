# Houge 安装部署

默认已提供 Docker 镜像构建发布，推荐使用 Docker 部署 Houge 相关服务。在阅读文档之前请先确保已安装 Docker
相关环境 [Get Docker](https://docs.docker.com/get-docker/) 。

## 先决条件

使用如下命令查看 Docker 版本信息，确认 Docker 已经安装。

```
$ docker --version
Docker version 20.10.2, build 2291f61
```

## 使用 Docker Compose 部署服务

如果未安装 Docker Compose 请参考 [Install Docker Compose](https://docs.docker.com/compose/install/) 文档安装。

Houge 已编写 [docker-compose.yml](https://gitee.com/kk70/tethys/blob/main/docker-compose.yml) 配置可快速帮助部署 Tethys 相关服务。

### 获取 docker-compose.yml

```
$ curl https://gitee.com/kk70/tethys/raw/main/docker-compose.yml > docker-compose.yml
```

### 启动服务

```
docker-compose up -d
```

### 查看日志
```
$ docker logs tethys-server-logic
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar

... 省略的日志

07:01:07.769 [reactor-tcp-epoll-1] INFO  top.yein.tethys.system.identifier.AbstractApplicationIdentifier 117 - 新增 ServerInstance: ServerInstance(id=10133, appName=tethys-logic, hostName=f66eb00b96ef, hostAddress=172.28.0.6, osName=Linux, osVersion=4.19.128-microsoft-standard, osArch=amd64, osUser=root, javaVmName=OpenJDK 64-Bit Server VM, javaVmVersion=11.0.11+9, javaVmVendor=Oracle Corporation, workDir=/app, pid=7, ver=0, createTime=null, checkTime=null)
07:01:07.950 [main] INFO  top.yein.tethys.logic.server.LogicServer 64 - Logic gRPC服务启动成功 [/0.0.0.0:11012]
```

```
$ docker logs tethys-server-rest
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar

... 省略的日志

07:01:07.768 [main] INFO  top.yein.tethys.rest.server.RestServer 77 - REST Server 启动完成 - 0.0.0.0:11019
```

```
$ docker logs tethys-server-ws
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar

... 省略的日志

07:01:05.520 [main] INFO  top.yein.tethys.ws.server.WsServer 70 - WS服务启动成功 [/0.0.0.0:11010]
```


使用 `docker logs` 命令查看 Houge 服务日志，当有上面的日志打印时代表 Houge 服务已启动成功。