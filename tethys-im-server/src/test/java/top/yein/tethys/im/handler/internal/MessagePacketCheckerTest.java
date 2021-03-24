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
package top.yein.tethys.im.handler.internal;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.MessageContentKind;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.util.YeinGid;

/**
 * {@link MessagePacketChecker} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessagePacketCheckerTest {

  @Test
  void checkPrivateMessagePacket() {
    var packet = new PrivateMessagePacket();
    packet.setMessageId(new YeinGid(0).toHexString());
    packet.setFrom(1L);
    packet.setTo(2L);
    packet.setContentKind(MessageContentKind.TEXT.getCode());
    packet.setContent("hello world");
    packet.setUrl("https://tethys.yein.top");
    packet.setCustomArgs("{}");
    MessagePacketChecker.check(packet);
  }

  @Test
  void checkGroupMessagePacket() {
    var packet = new GroupMessagePacket();
    packet.setMessageId(new YeinGid(0).toHexString());
    packet.setFrom(0L);
    packet.setTo(1L);
    packet.setContentKind(MessageContentKind.TEXT.getCode());
    packet.setContent("hello world");
    packet.setUrl("https://tethys.yein.top");
    packet.setCustomArgs("{}");
    MessagePacketChecker.check(packet);
  }

  @Test
  void checkMsgId() {
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkMessageId(null))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkMessageId(""))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
  }

  @Test
  void checkTo() {
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkTo(null))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkTo(null))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
  }

  @Test
  void checkKind() {
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkContentKind(-1))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
  }

  @Test
  void checkContent() {
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkContent(null))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkContent(""))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
  }
}
