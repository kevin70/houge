# Tethys 安装部署

默认已提供 Docker 镜像构建发布，推荐使用 Docker 部署 Tethys 相关服务。在阅读文档之前请先确保已安装 Docker
相关环境 [Get Docker](https://docs.docker.com/get-docker/) 。

## 先决条件

使用如下命令查看 Docker 版本信息，确认 Docker 已经安装。

```
$ docker --version
Docker version 20.10.2, build 2291f61
```

## 使用 Docker Compose 部署服务

如果未安装 Docker Compose 请参考 [Install Docker Compose](https://docs.docker.com/compose/install/) 文档安装。

Tethys 已编写 [docker-compose.yml](https://gitee.com/kk70/tethys/blob/main/docker-compose.yml) 配置可快速帮助部署 Tethys 相关服务。

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
$ docker logs tethys-im
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar
... 省略的日志
05:21:20.843 [main] INFO  top.yein.tethys.im.server.ImServer 75 - IM Server 启动完成 - 0.0.0.0:11010
05:21:20.844 [main] INFO  top.yein.tethys.im.main.ImMain 36 - tethys-im 服务启动成功 fid=108932
```

```
$ docker logs tethys-rest
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /app/app.jar
... 省略的日志
05:21:23.955 [main] INFO  top.yein.tethys.rest.server.RestServer 53 - REST Server 启动完成 - 0.0.0.0:11019
05:21:23.956 [main] INFO  top.yein.tethys.rest.main.RestMain 36 - tethys-rest 服务启动成功 fid=30931
```

使用 `docker logs` 命令查看 Tethys 服务日志，当有上面的日志打印时代表 Tethys 服务已启动成功。