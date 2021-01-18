package top.yein.tethys.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * {@link YeinGid} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class YeinGidTest {

  @Test
  void makeYeinGid() {
    short f1 = 88;
    var f2 = 5432;
    var gid = new YeinGid(f1, f2);
    var hexString1 = gid.toAsciiString();
    var hexString2 = gid.toAsciiString();
    assertThat(hexString1).hasSize(YeinGid.YEIN_GID_LENGTH);
    assertThat(hexString1).isEqualTo(hexString2);
    assertThat(hexString1).isEqualTo(gid.toString());
  }

  @ValueSource(
      strings = {"188,12345", "2,12345", "2,45678", "9,123", "255,65535", "0,65535", "255,0"})
  @ParameterizedTest
  void fromString(String p1) {
    var args = p1.split(",");
    var f1 = Integer.parseInt(args[0]);
    var f2 = Integer.parseInt(args[1]);
    var gid = new YeinGid(f1, f2);
    var hexString = gid.toAsciiString();
    var parsedGid = YeinGid.fromString(hexString);
    assertSoftly(
        s -> {
          s.assertThat(parsedGid.getVersion()).as("version").isEqualTo(gid.getVersion());
          s.assertThat(parsedGid.getSeconds()).as("seconds").isEqualTo(gid.getSeconds());
          s.assertThat(parsedGid.getSeq()).as("seq").isEqualTo(gid.getSeq());
          s.assertThat(parsedGid.getF1()).as("f1").isEqualTo(gid.getF1());
          s.assertThat(parsedGid.getF2()).as("f2").isEqualTo(gid.getF2());
          s.assertThat(parsedGid.toString()).as("hexString").isEqualTo(hexString);
        });
  }

  @Test
  void illegalYeinGid() {
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString(null));
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString(""));
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString("4S27BZZ8ZZR*7EMG"));
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString("4S27BZZ#ZZR*7EMG&"));
  }

  @Test
  void illegalF1AndF2() {
    assertThatIllegalArgumentException().isThrownBy(() -> new YeinGid(-1, 88));
    assertThatIllegalArgumentException().isThrownBy(() -> new YeinGid(256, 88));
    assertThatIllegalArgumentException().isThrownBy(() -> new YeinGid(88, -1));
    assertThatIllegalArgumentException().isThrownBy(() -> new YeinGid(88, 65536));
  }
}
