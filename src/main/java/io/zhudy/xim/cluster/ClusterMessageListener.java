package io.zhudy.xim.cluster;

import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.zhudy.xim.session.SessionGroupManager;
import io.zhudy.xim.session.SessionManager;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.scheduler.Schedulers;

/**
 * Tmessage 监听.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class ClusterMessageListener {

  private static final String PUSH_ALL_PATTERN = "push:a";
  private static final String PUSH_ROOM_PATTERN = "push:r:*";
  private static final String PUSH_USER_PATTERN = "push:u:*";
  private static final String PRIVATE_PATTERN = "private:*";
  private static final String GROUP_PATTERN = "group:*";

  private static final String[] patterns = {
    // 模板消息
    PUSH_ALL_PATTERN,
    PUSH_ROOM_PATTERN,
    PUSH_USER_PATTERN,
    // 私聊
    PRIVATE_PATTERN,
    // 群组消息
    GROUP_PATTERN
  };

  private final RedisClient redisClient;
  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;
  private final StatefulRedisPubSubConnection<byte[], byte[]> subConnection;
  private final RedisPubSubAdapter redisPubSubAdapter;

  @Inject
  public ClusterMessageListener(
      RedisClient redisClient,
      SessionManager sessionManager,
      SessionGroupManager sessionGroupManager) {
    this.redisClient = redisClient;
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
    this.subConnection = redisClient.connectPubSub(new ByteArrayCodec());
    this.redisPubSubAdapter = new RedisMessageHandler();
  }

  /** 启动监听. */
  public void start() {
    this.subConnection.sync().psubscribe(patterns0());
    this.subConnection.addListener(this.redisPubSubAdapter);
  }

  /** 停止监听. */
  public void stop() {
    this.subConnection.removeListener(this.redisPubSubAdapter);
    this.subConnection.sync().punsubscribe(patterns0());
    this.subConnection.close();
  }

  private byte[][] patterns0() {
    return Arrays.asList(patterns).stream()
        .map(s -> s.getBytes(StandardCharsets.UTF_8))
        .collect(Collectors.toList())
        .toArray(new byte[][] {});
  }

  private class RedisMessageHandler extends RedisPubSubAdapter<byte[], byte[]> {

    @Override
    public void message(byte[] channel, byte[] message) {
      var ns = new String(channel, StandardCharsets.UTF_8);
      var i = -1;
      if (PUSH_ALL_PATTERN.equals(ns)) {
        sessionManager
            .all()
            .flatMap(s -> s.send(toByteBuf(message)))
            .subscribeOn(Schedulers.parallel())
            .subscribe();
      } else if ((i = ns.indexOf(PUSH_USER_PATTERN)) >= 0) {
        var uid = ns.substring(i + PUSH_USER_PATTERN.length() + 1);
        sessionManager
            .findByUid(uid)
            .flatMap(s -> s.send(toByteBuf(message)))
            .subscribeOn(Schedulers.parallel())
            .subscribe();
      } else if ((i = ns.indexOf(PUSH_ROOM_PATTERN)) >= 0) {
        var groupId = ns.substring(i + PUSH_ROOM_PATTERN.length() + 1);
        sessionGroupManager
            .findByGroupId(groupId)
            .flatMap(s -> s.send(toByteBuf(message)))
            .subscribeOn(Schedulers.parallel())
            .subscribe();
      } else if ((i = ns.indexOf(PRIVATE_PATTERN)) >= 0) { // 私人消息
        var uid = ns.substring(i + PUSH_USER_PATTERN.length() + 1);
        sessionManager
            .findByUid(uid)
            .flatMap(s -> s.send(toByteBuf(message)))
            .subscribeOn(Schedulers.parallel())
            .subscribe();
      } else if ((i = ns.indexOf(GROUP_PATTERN)) >= 0) {
        var groupId = ns.substring(i + GROUP_PATTERN.length() + 1);
        sessionGroupManager
            .findByGroupId(groupId)
            .flatMap(s -> s.send(toByteBuf(message)))
            .subscribeOn(Schedulers.parallel())
            .subscribe();
      } else {
        log.error(
            "[集群消息] 未找到 redis channel 处理方法 [channel={}, message={}]",
            channel,
            new String(message, StandardCharsets.UTF_8));
      }
    }

    private ByteBuf toByteBuf(byte[] message) {
      var byteBuf = ByteBufAllocator.DEFAULT.directBuffer();
      byteBuf.writeBytes(message);
      return byteBuf;
    }
  }
}
