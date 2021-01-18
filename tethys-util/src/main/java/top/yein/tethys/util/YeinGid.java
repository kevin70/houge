package top.yein.tethys.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式全局唯一 ID.
 *
 * <p>YeinGid 采用 <b>80bit</b> 组成, 采用 32 进制编码为 16 个字符的全局唯一 ID.
 *
 * <table border="1">
 *   <tr>
 *     <td>version</td>
 *     <td>seconds</td>
 *     <td>sequence</td>
 *     <td>flag 1</td>
 *     <td>flag 2</td>
 *   </tr>
 *   <tr>
 *     <td>4bit</td>
 *     <td>32bit</td>
 *     <td>20bit</td>
 *     <td>8bit</td>
 *     <td>16bit</td>
 *   </tr>
 * </table>
 *
 * 每 5bit 编码为一个字符, {@link YeinGid } 总长度为 16 个字符.
 *
 * @author KK (kzou227@qq.com)
 */
public class YeinGid {

  public static final int YEIN_GID_LENGTH = 16;
  /** 版本号掩码. */
  private static final int VERSION_MASK = (int) (Math.pow(2, 4) - 1);
  /** ID 生成时间掩码(秒). */
  private static final int SECONDS_MASK = (int) (Math.pow(2, 32) - 1);
  /** 序列掩码. */
  private static final int SEQ_MASK = (int) (Math.pow(2, 20) - 1);
  /** flag 1 标志一掩码. */
  private static final int F1_MASK = (int) (Math.pow(2, 8) - 1);
  /** flag 2 标志二掩码. */
  private static final int F2_MASK = (int) (Math.pow(2, 16) - 1);

  /** 当前版本号. */
  private static final int VERSION = 1;

  private static final char[] DIGITS = {
    '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L',
    'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
  };
  private static final int SEQ_BOUND = SEQ_MASK + 1;
  private static final AtomicInteger SEQ = new AtomicInteger(new SecureRandom().nextInt(SEQ_MASK));

  // 4bit
  private int version;
  // 32bit
  private long seconds;
  // 20bit
  private int seq;
  // 8bit
  private int f1;
  // 16bit
  private int f2;
  // 编码后的字符串
  private String asciiString;

  private YeinGid(int version, long seconds, int seq, int f1, int f2, String asciiString) {
    this.version = version;
    this.seconds = seconds;
    this.seq = seq;
    this.f1 = f1;
    this.f2 = f2;
    this.asciiString = asciiString;
  }

  /**
   * 采用 2 个标志构建全局唯一 ID.
   *
   * @param f1 标志一: 一个 0-255 的数字
   * @param f2 标志二: 一个 0-65535 的数字
   */
  public YeinGid(int f1, int f2) {
    if (f1 < 0 || f1 > F1_MASK) {
      throw new IllegalArgumentException("非 0-255 区间内的数字[f1=" + f1 + "]");
    }
    if (f2 < 0 || f2 > F2_MASK) {
      throw new IllegalArgumentException("非 0-65535 区间内的数字[f1=" + f1 + "]");
    }
    SEQ.compareAndSet(SEQ_BOUND, 0);
    this.version = VERSION;
    this.seconds = Instant.now().getEpochSecond();
    this.seq = SEQ.incrementAndGet();
    this.f1 = f1;
    this.f2 = f2;
  }

  /**
   * 返回 YeinGid 的版本号.
   *
   * @return 版本号
   */
  public int getVersion() {
    return version;
  }

  /**
   * 返回 YeinGid 创建时间(unix timestamp).
   *
   * @return 创建时间
   */
  public long getSeconds() {
    return seconds;
  }

  /**
   * 返回序列号.
   *
   * @return 序列号
   */
  public int getSeq() {
    return seq;
  }

  /**
   * 返回标志一.
   *
   * @return 标志一
   */
  public int getF1() {
    return f1;
  }

  /**
   * 返回标志二.
   *
   * @return 标志二
   */
  public int getF2() {
    return f2;
  }

  /**
   * 从编码字符串解析 {@link YeinGid}.
   *
   * @param asciiString 编码字符串
   * @return YeinGid
   */
  public static YeinGid fromString(String asciiString) {
    if (asciiString == null || asciiString.isEmpty()) {
      throw new IllegalArgumentException("YeinGid: asciiString 参数为 null");
    }
    if (asciiString.length() > YEIN_GID_LENGTH) {
      throw new IllegalArgumentException("YeinGid: 非法的 asciiString 参数 \"" + asciiString + "\"");
    }

    var longs = new long[YEIN_GID_LENGTH];
    var chars = asciiString.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      var idx = Arrays.binarySearch(DIGITS, chars[i]);
      if (idx < 0) {
        throw new IllegalArgumentException("YeinGid: 非法的 asciiString 编码 \"" + asciiString + "\"");
      }
      longs[i] = idx;
    }

    long highBits =
        longs[0] << 59
            | longs[1] << 54
            | longs[2] << 49
            | longs[3] << 44
            | longs[4] << 39
            | longs[5] << 34
            | longs[6] << 29
            | longs[7] << 24
            | longs[8] << 19
            | longs[9] << 14
            | longs[10] << 9
            | longs[11] << 4
            | (longs[12] >> 1 & 0xf);
    long lowBits = (longs[12] & 0x1) << 63 | longs[13] << 58 | longs[14] << 53 | longs[15] << 48;

    int version = (int) (highBits >> 60 & VERSION_MASK);
    long seconds = highBits >> 28 & SECONDS_MASK;
    int seq = (int) (highBits >> 8 & SEQ_MASK);
    int f1 = (int) (highBits & F1_MASK);
    int f2 = (int) (lowBits >> 48 & F2_MASK);
    return new YeinGid(version, seconds, seq, f1, f2, asciiString);
  }

  /**
   * 返回 YeinGid 编码后的字符串.
   *
   * @return 编码字符串
   */
  public String toAsciiString() {
    if (asciiString != null) {
      return asciiString;
    }

    char[] value = new char[YEIN_GID_LENGTH];
    long highBits = ((long) VERSION) << 60 | seconds << 28 | seq << 8 | f1;
    long lowBits = ((long) f2) << 48;

    value[0] = DIGITS[(int) (highBits >> 59 & 0x1f)];
    value[1] = DIGITS[(int) (highBits >> 54 & 0x1f)];
    value[2] = DIGITS[(int) (highBits >> 49 & 0x1f)];
    value[3] = DIGITS[(int) (highBits >> 44 & 0x1f)];
    value[4] = DIGITS[(int) (highBits >> 39 & 0x1f)];
    value[5] = DIGITS[(int) (highBits >> 34 & 0x1f)];
    value[6] = DIGITS[(int) (highBits >> 29 & 0x1f)];
    value[7] = DIGITS[(int) (highBits >> 24 & 0x1f)];
    value[8] = DIGITS[(int) (highBits >> 19 & 0x1f)];
    value[9] = DIGITS[(int) (highBits >> 14 & 0x1f)];
    value[10] = DIGITS[(int) (highBits >> 9 & 0x1f)];
    value[11] = DIGITS[(int) (highBits >> 4 & 0x1f)]; // highBits 还剩余 4bits 需要在后续编码
    value[12] = DIGITS[(int) ((highBits & 0xf) << 1 | lowBits >> 63 & 0x1)];
    value[13] = DIGITS[(int) (lowBits >> 58 & 0x1f)];
    value[14] = DIGITS[(int) (lowBits >> 53 & 0x1f)];
    value[15] = DIGITS[(int) (lowBits >> 48 & 0x1f)];

    this.asciiString = new String(value);
    return asciiString;
  }

  @Override
  public String toString() {
    return toAsciiString();
  }
}
