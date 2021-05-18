FROM openjdk:11-jre-buster
LABEL maintainer="kzou227@qq.com"

WORKDIR /app
COPY app.jar /app/app.jar

# `/opt/run-java-options` 挂载 JVM 配置参数文件可动态修改容器的 JVM 参数
# `/etc/houge/houge.conf` 挂载 Houge 项目配置参数
VOLUME ["/opt", "/etc/houge"]

# Houge WebSocket 服务访问的端口
EXPOSE 11010

# Java 应用程序的通用启动脚本，特别是为从容器中运行而精心编制的。
# https://github.com/fabric8io-images/run-java-sh
ENV RUN_JAVA_SH_VERSION=1.3.8 \
  JAVA_APP_JAR=app.jar
RUN wget https://github.com/fabric8io-images/run-java-sh/archive/${RUN_JAVA_SH_VERSION}.tar.gz \
  && tar -xzf ${RUN_JAVA_SH_VERSION}.tar.gz \
  && mv run-java-sh-${RUN_JAVA_SH_VERSION}/fish-pepper/run-java-sh/fp-files/run-java.sh . \
  && rm -rf run-java-sh-${RUN_JAVA_SH_VERSION} ${RUN_JAVA_SH_VERSION}.tar.gz

CMD ./run-java.sh