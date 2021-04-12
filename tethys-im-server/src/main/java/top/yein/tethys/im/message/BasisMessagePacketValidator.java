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
package top.yein.tethys.im.message;

import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.Nil;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.message.MessagePacketValidator;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.service.GroupService;
import top.yein.tethys.service.UserService;

/**
 * 基本的消息校验器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class BasisMessagePacketValidator implements MessagePacketValidator {

  private final UserService userService;
  private final GroupService groupService;

  /**
   * 能被 IoC 容器使用的构造函数.
   *
   * @param userService 用户服务对象
   * @param groupService 群组服务对象
   */
  @Inject
  public BasisMessagePacketValidator(UserService userService, GroupService groupService) {
    this.userService = userService;
    this.groupService = groupService;
  }

  @Override
  public Mono<Nil> validate(MessagePacket packet) {
    // 消息校验
    var kind = MessageKind.forCode(packet.getKind());
    if (kind.isGroup()) {
      return groupService
          .existsById(packet.getTo())
          .switchIfEmpty(
              Mono.error(
                  () -> {
                    log.warn("未找到群组[{}] {}", packet.getTo(), packet);
                    return new BizCodeException(BizCodes.C3630, "未找到群组[" + packet.getTo() + "]");
                  }));
    } else {
      return userService
          .existsById(packet.getTo())
          .switchIfEmpty(
              Mono.error(
                  () -> {
                    log.warn("未找到用户[{}] {}", packet.getTo(), packet);
                    return new BizCodeException(BizCodes.C3630, "未找到用户[" + packet.getTo() + "]")
                        .addContextValue("to", packet.getTo());
                  }));
    }
  }
}
