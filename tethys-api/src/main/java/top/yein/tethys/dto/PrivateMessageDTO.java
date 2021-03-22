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
package top.yein.tethys.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 私人消息 DTO.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class PrivateMessageDTO {

  /** 消息 ID 全局唯一. */
  private String id;
  /** 发送人 ID. */
  private String senderId;
  /** 接收人 ID. */
  private String receiverId;
  /** 消息类型. */
  private int kind;
  /** 消息内容. */
  private String content;
  /** 统一资源定位器. */
  private String url;
  /** 自定义参数. */
  private String customArgs;
  /** 是否未读. */
  private int unread;
  /** 创建时间. */
  private LocalDateTime createTime;
}
