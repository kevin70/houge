FROM adoptopenjdk/openjdk11:alpine-jre

RUN apk --no-cache add --update bash openssl

# Add the flyway user and step in the directory
RUN addgroup flyway \
    && adduser -S -h /flyway -D -G flyway flyway
WORKDIR /flyway

# Change to the flyway user
USER flyway

ENV FLYWAY_VERSION 7.5.3

RUN wget https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && tar -xzf flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && mv flyway-${FLYWAY_VERSION}/* . \
  && rm flyway-commandline-${FLYWAY_VERSION}.tar.gz

# See https://github.com/eficode/wait-for
ENV WAIT_FOR_VERSION 2.0.0
RUN wget https://github.com/eficode/wait-for/archive/v${WAIT_FOR_VERSION}.tar.gz \
  && tar -xzf v${WAIT_FOR_VERSION}.tar.gz \
  && mv wait-for-${WAIT_FOR_VERSION}/wait-for . \
  && rm v${WAIT_FOR_VERSION}.tar.gz

# 将所有的 SQL 文件拷贝至 flyway/sql 目录中
COPY migrations/* sql/

ENV PATH="/flyway:${PATH}"

ENTRYPOINT ["flyway"]
CMD ["-?"]