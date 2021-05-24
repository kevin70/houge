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
package cool.houge.service.vo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cool.houge.constants.MessageContentType;
import cool.houge.constants.MessageKind;
import lombok.Data;

/**
 * 消息发送 VO.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class SendMessageVO {

  /**
   * 消息类型.
   *
   * @see MessageKind
   */
  private int kind;
  /** 消息接收者. */
  private long to;
  /** 消息内容. */
  private String content;
  /**
   * 消息内容类型.
   *
   * @see MessageContentType
   */
  private int contentType;
  /** 扩展参数. */
  private @JsonUnwrapped String extraArgs;
}
