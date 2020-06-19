package io.zhudy.xim.packet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Packet 解析及响应规范定义.
 *
 * <p>所有 IM 消息会话都需要继续该接口, 该接口定义了标准的解析及响应规范.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "ns")
@JsonPropertyOrder("ns")
public interface Packet {

  /** 全局消息分组 ID. */
  String GROUP_ID_ALL = "all";

  /**
   * 命名空间.
   *
   * @return 命名空间
   * @see Namespaces
   */
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String getNs();
}
