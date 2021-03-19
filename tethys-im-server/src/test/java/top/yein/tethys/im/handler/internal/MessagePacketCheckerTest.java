package top.yein.tethys.im.handler.internal;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.MessageKind;
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
    packet.setContentKind(MessageKind.TEXT.getCode());
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
    packet.setContentKind(MessageKind.TEXT.getCode());
    packet.setContent("hello world");
    packet.setUrl("https://tethys.yein.top");
    packet.setCustomArgs("{}");
    MessagePacketChecker.check(packet);
  }

  @Test
  void checkMsgId() {
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkMsgId(null))
        .matches((ex) -> ex.getBizCode() == BizCodes.C3600);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> MessagePacketChecker.checkMsgId(""))
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
        .isThrownBy(() -> MessagePacketChecker.checkKind(-1))
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
