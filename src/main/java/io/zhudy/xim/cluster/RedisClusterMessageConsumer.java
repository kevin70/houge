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

import static java.nio.charset.StandardCharsets.UTF_8;

import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.zhudy.xim.helper.PacketHelper;
import io.zhudy.xim.packet.MsgPacket;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.router.MessageRouter;
import java.io.IOException;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.scheduler.Schedulers;

/**
 * Redis 集群消息消费者.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class RedisClusterMessageConsumer implements ClusterMessageConsumer {

  private final MessageRouter messageRouter;
  private final RedisClient redisClient;

  private volatile StatefulRedisPubSubConnection<byte[], byte[]> redisSubConnection;
  private volatile boolean started;
  private volatile boolean stopped;

  @Inject
  public RedisClusterMessageConsumer(RedisClient redisClient, MessageRouter messageRouter) {
    this.redisClient = redisClient;
    this.messageRouter = messageRouter;
  }

  @Override
  public synchronized void start() {
    if (started) {
      throw new IllegalStateException("RedisClusterMessageConsumer 已经启动");
    }
    if (stopped) {
      throw new IllegalStateException("RedisClusterMessageConsumer 已经启动停止，无法重新启动");
    }
    log.info("启动中...");

    // 创建 redis 连接
    this.redisSubConnection = redisClient.connectPubSub(ByteArrayCodec.INSTANCE);

    // 监听集群消息
    this.redisSubConnection.addListener(
        new RedisPubSubAdapter<>() {

          @Override
          public void message(byte[] channelBytes, byte[] message) {
            var channel = new String(channelBytes, UTF_8);
            ChannelType channelType = null;
            for (ChannelType ct : ChannelType.values()) {
              if (channel.startsWith(ct.getPrefix())) {
                channelType = ct;
              }
            }

            if (channelType == null) {
              log.error("未找到的集群消息通道类型 channel={}, message={}", channel, new String(message, UTF_8));
              return;
            }

            Packet packet;
            try {
              packet = PacketHelper.getObjectMapper().readValue(message, Packet.class);
            } catch (IOException e) {
              log.error("解析集群消息失败 channel={}, message={}", channel, new String(message, UTF_8));
              return;
            }

            log.debug("集群消息路由 channel={}, packet={}", channel, packet);
            messageRouter
                .route((MsgPacket) packet)
                .doOnSuccess(unused -> log.debug("集群消息路由成功 channel={}, packet={}", channel, packet))
                .doOnError(e -> log.error("集群消息处理失败 channel={}, packet={}", channel, packet, e))
                .publishOn(Schedulers.parallel())
                .subscribeOn(Schedulers.parallel())
                .subscribe();
          }
        });

    log.info("启动完成 [redisSubConnection={}]", redisSubConnection);
    this.started = true;
  }

  @Override
  public synchronized void stop() {
    if (!started) {
      throw new IllegalStateException("未启动的不能停止");
    }
    if (stopped) {
      throw new IllegalStateException("已停止的");
    }
    if (redisSubConnection != null) {
      redisSubConnection.close();
    }

    log.info("停止完成");
    this.stopped = true;
  }
}
