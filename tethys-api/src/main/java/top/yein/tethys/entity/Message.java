/*
 * Copyright 2019-2021 the original author or authors
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
package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code t_message} 消息表.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  /** 系统消息类型. */
  public static final int KIND_SYSTEM = 1;
  /** 私聊消息类型. */
  public static final int KIND_PRIVATE = 2;
  /** 群组消息类型. */
  public static final int KIND_GROUP = 3;

  /** 消息 ID. */
  private String id;
  /** 发送人 ID. */
  private Long senderId;
  /** 接收人 ID. */
  private Long receiverId;
  /** 群主 ID. */
  private Long groupId;
  /**
   * 消息类型.
   *
   * <ul>
   *   <li>{@code 1}: 系统消息
   *   <li>{@code 2}: 私聊消息
   *   <li>{@code 3}: 群组消息
   * </ul>
   */
  private Integer kind;
  /** 消息内容. */
  private String content;
  /**
   * 消息内容类型.
   *
   * <ul>
   *   <li>{@code 1}: 普通文本消息
   *   <li>{@code 2}: 图片消息
   *   <li>{@code 3}: 音频消息
   *   <li>{@code 4}: 视频消息
   * </ul>
   */
  private Integer contentKind;
  /** 消息内容资源. */
  private String url;
  /** 自定义参数. */
  private String customArgs;
  /**
   * 消息是否未读.
   *
   * <ul>
   *   <li>{@code 1}: 未读
   *   <li>{@code 2}: 已读
   * </ul>
   */
  private Integer unread;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 更新时间. */
  private LocalDateTime updateTime;
}
