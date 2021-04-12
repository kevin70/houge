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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.Nil;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.packet.SimpleMessagePacket;
import top.yein.tethys.service.GroupService;
import top.yein.tethys.service.UserService;

/**
 * {@link BasisMessagePacketValidator} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class BasisMessagePacketValidatorTest {

  @Test
  void validate() {
    var userService = mock(UserService.class);
    var groupService = mock(GroupService.class);
    when(userService.existsById(anyLong())).thenReturn(Nil.mono());
    when(groupService.existsById(anyLong())).thenReturn(Nil.mono());

    var validator = new BasisMessagePacketValidator(userService, groupService);

    var packet = new SimpleMessagePacket();
    packet.setKind(MessageKind.P_MESSAGE.getCode());
    packet.setTo(1);
    StepVerifier.create(validator.validate(packet))
        .expectNext(Nil.INSTANCE)
        .expectComplete()
        .verify();

    var packet2 = new SimpleMessagePacket();
    packet2.setKind(MessageKind.G_MESSAGE.getCode());
    packet2.setTo(1);
    StepVerifier.create(validator.validate(packet2))
        .expectNext(Nil.INSTANCE)
        .expectComplete()
        .verify();

    // 校验 mock 函数执行
    verify(userService).existsById(anyLong());
    verify(groupService).existsById(anyLong());
  }

  @Test
  void validate_empty() {
    var userService = mock(UserService.class);
    var groupService = mock(GroupService.class);
    when(userService.existsById(anyLong())).thenReturn(Mono.empty());
    when(groupService.existsById(anyLong())).thenReturn(Mono.empty());

    var validator = new BasisMessagePacketValidator(userService, groupService);

    var packet = new SimpleMessagePacket();
    packet.setKind(MessageKind.P_MESSAGE.getCode());
    packet.setTo(1);
    StepVerifier.create(validator.validate(packet)).expectError(BizCodeException.class).verify();

    var packet2 = new SimpleMessagePacket();
    packet2.setKind(MessageKind.G_MESSAGE.getCode());
    packet2.setTo(1);
    StepVerifier.create(validator.validate(packet2)).expectError(BizCodeException.class).verify();

    // 校验 mock 函数执行
    verify(userService).existsById(anyLong());
    verify(groupService).existsById(anyLong());
  }
}
