/*
 * Copyright 2019-2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zhudy.xim.cluster;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.zhudy.xim.helper.PacketHelper;
import io.zhudy.xim.packet.MsgPacket;
import io.zhudy.xim.packet.PrivateMsgPacket;
import io.zhudy.xim.router.MessageRouter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import reactor.core.publisher.Mono;

/**
 * {@link RedisClusterMessageConsumer} 单元测试.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
class RedisClusterMessageConsumerTests {

  @Container
  private GenericContainer redis =
      new GenericContainer("redis:5.0.9-alpine").withExposedPorts(6379);

  @BeforeEach
  void setUp() {
    redis.start();
  }

  @AfterEach
  void termDown() {
    redis.stop();
  }

  @Test
  void consumePacket() throws InterruptedException, JsonProcessingException {
    var redisUri = RedisURI.create(redis.getHost(), redis.getFirstMappedPort());
    var redisClient = RedisClient.create(redisUri);
    var mockedMessageRouter = Mockito.mock(MessageRouter.class);

    var channel = ChannelType.PRIVATE.getPrefix() + "junit";
    var sourcePacket = new PrivateMsgPacket();
    sourcePacket.setFrom("from-junit");
    sourcePacket.setTo("to-junit");
    sourcePacket.setContent("Hello Test!!!");

    var consumer = new RedisClusterMessageConsumer(redisClient, mockedMessageRouter);
    consumer.start();

    // RedisClusterMessageConsumer 内部的消费者
    StatefulRedisPubSubConnection<byte[], byte[]> subConnection =
        Whitebox.getInternalState(consumer, "redisSubConnection");
    subConnection
        .reactive()
        .subscribe(channel.getBytes(StandardCharsets.UTF_8))
        .block(Duration.ofSeconds(5));

    // 生产消息
    var pubConnection = redisClient.connectPubSub();
    pubConnection
        .reactive()
        .publish(channel, PacketHelper.getObjectMapper().writeValueAsString(sourcePacket))
        .block(Duration.ofSeconds(5));

    var cdl = new CountDownLatch(1);
    var acPacket = ArgumentCaptor.forClass(MsgPacket.class);
    Mockito.when(mockedMessageRouter.route(acPacket.capture()))
        .thenAnswer(
            (Answer<Mono<Void>>)
                invocation -> {
                  cdl.countDown();
                  return Mono.empty();
                });

    var rs = cdl.await(5, TimeUnit.SECONDS);
    assertThat(rs).isTrue();

    var packet = acPacket.getValue();
    assertThat(packet).isEqualTo(sourcePacket);
    consumer.stop();
  }
}
