package io.zhudy.xim.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import io.zhudy.xim.packet.GroupMsgPacket;
import io.zhudy.xim.packet.GroupSubPacket;
import io.zhudy.xim.packet.GroupUnsubPacket;
import io.zhudy.xim.packet.Namespaces;
import io.zhudy.xim.packet.PrivateMsgPacket;

/**
 * Packet 工具类.
 *
 * <p>该工具类仅为 {@code Packet} 类型服务, 非 Packet 类型不应该使用此类.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class PacketHelper {

  /**
   * Packet JSON 序列化与反序列化实例.
   *
   * <p>注意: <b>该对象经过特殊的配置, 序列化与反序列化非 {@code Packet} 对象时无法保证其正确性.</b>
   */
  public static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModules(new AfterburnerModule());

    MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);

    // 注册 Packet 子类以解析实际的包体
    MAPPER.registerSubtypes(new NamedType(PrivateMsgPacket.class, Namespaces.PRIVATE_MSG));
    MAPPER.registerSubtypes(new NamedType(GroupMsgPacket.class, Namespaces.GROUP_MSG));
    MAPPER.registerSubtypes(new NamedType(GroupSubPacket.class, Namespaces.GROUP_SUBSCRIBE));
    MAPPER.registerSubtypes(new NamedType(GroupUnsubPacket.class, Namespaces.GROUP_UNSUBSCRIBE));
  }
}
