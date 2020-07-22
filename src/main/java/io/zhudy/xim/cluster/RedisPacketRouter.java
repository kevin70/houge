package io.zhudy.xim.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.zhudy.xim.helper.PacketHelper;
import io.zhudy.xim.packet.GroupMsgPacket;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.packet.PrivateMsgPacket;
import io.zhudy.xim.router.PacketRouter;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.inject.Named;
import reactor.core.publisher.Mono;

/**
 * Redis 集群消息分发.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class RedisPacketRouter implements PacketRouter {

  private final PacketRouter localPacketRouter;
  private final StatefulRedisPubSubConnection<byte[], byte[]> pubConnection;

  @Inject
  public RedisPacketRouter(
      @Named("localPacketRouter") PacketRouter localPacketRouter, RedisClient redisClient) {
    this.localPacketRouter = localPacketRouter;
    this.pubConnection = redisClient.connectPubSub(new ByteArrayCodec());
  }

  @Override
  public Mono<Void> route(Mono<Packet> packetMono) {
    return packetMono.flatMap(this::route0).then(localPacketRouter.route(packetMono));
  }

  private Mono<Void> route0(Packet packet) {
    // TODO: 需要优化代码中的常量
    final String channel;
    if (packet instanceof PrivateMsgPacket) {
      channel = "private:" + ((PrivateMsgPacket) packet).getTo();
    } else if (packet instanceof GroupMsgPacket) {
      channel = "group:" + ((GroupMsgPacket) packet).getTo();
    } else {
      return Mono.empty();
    }

    try {
      // TODO: 后期可优化 JSON 序列化逻辑, 利用上下文中的 ByteBuf 可减少 JSON 序列化消耗
      return pubConnection
          .reactive()
          .publish(
              channel.getBytes(StandardCharsets.UTF_8),
              PacketHelper.MAPPER.writeValueAsBytes(packet))
          .then();
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("JSON 转换错误", e);
    }
  }
}
