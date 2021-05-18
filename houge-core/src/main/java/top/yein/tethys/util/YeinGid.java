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
package top.yein.tethys.util;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式全局唯一 ID.
 *
 * <p>YeinGid 采用 <b>75bits</b> 组成, 基于<b>32进制</b>编码的15字节的字符串.
 *
 * <table border="1">
 *   <tr>
 *     <td>version</td>
 *     <td>timestamp</td>
 *     <td>fid</td>
 *     <td>sequence</td>
 *   </tr>
 *   <tr>
 *     <td>3bits</td>
 *     <td>32bits</td>
 *     <td>17bits</td>
 *     <td>23bits</td>
 *   </tr>
 * </table>
 *
 * 每 {@code 5bits }编码为一个字符, {@link YeinGid }总长度为15字节.
 *
 * @author KK (kzou227@qq.com)
 */
public class YeinGid {

  /** 32进制编码的字母表. */
  private static final char[] DIGITS = {
    '2', '3', '4', '5', '6', '7', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
  };

  public static final int YEIN_GID_LENGTH = 15;
  /** 版本号掩码. */
  private static final int VERSION_MASK = -1 ^ (-1 << 3);
  /** 时间戳(秒). */
  private static final int SECONDS_MASK = Integer.MAX_VALUE;
  /** 标识掩码. */
  public static final int FID_MASK = -1 ^ (-1 << 17);
  /** 序列掩码. */
  private static final int SEQ_MASK = -1 ^ (-1 << 23);

  /** 当前版本号. */
  private static final int CURRENT_VERSION = 1;

  private static final int SEQ_BOUND = SEQ_MASK + 1;
  private static final AtomicInteger SEQUENCE = new AtomicInteger(new SecureRandom().nextInt(SEQ_MASK));

  private int version;
  private int timestamp;
  private int fid;
  private int seq;

  // 32进制编码的字符串
  private String hexString;

  private YeinGid(int version, int timestamp, int fid, int seq, String hexString) {
    this.version = version;
    this.timestamp = timestamp;
    this.fid = fid;
    this.seq = seq;
    this.hexString = hexString;
  }

  /**
   * 采用标识构建全局唯一 ID.
   *
   * @param fid 标识 {@code 0-131071 }区间内的数字
   */
  public YeinGid(int fid) {
    if (fid < 0 || fid > FID_MASK) {
      throw new IllegalArgumentException("非 0-131071 区间内的数字[fid=" + fid + "]");
    }
    SEQUENCE.compareAndSet(SEQ_BOUND, 0);
    this.version = CURRENT_VERSION;
    this.timestamp = (int) (System.currentTimeMillis() / 1000);
    this.fid = fid;
    this.seq = SEQUENCE.incrementAndGet();
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
  public int getTimestamp() {
    return timestamp;
  }

  /**
   * 返回标识.
   *
   * @return 标识
   */
  public int getFid() {
    return fid;
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
   * 从编码字符串解析 {@link YeinGid}.
   *
   * @param hexString 32进制编码的字符串
   * @return YeinGid
   */
  public static YeinGid fromString(String hexString) {
    if (hexString == null || hexString.isEmpty()) {
      throw new IllegalArgumentException("YeinGid: hexString 参数为 null");
    }
    if (hexString.length() > YEIN_GID_LENGTH) {
      throw new IllegalArgumentException("YeinGid: 非法的 hexString 参数 \"" + hexString + "\"");
    }

    var longs = new long[YEIN_GID_LENGTH];
    var chars = hexString.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      var idx = Arrays.binarySearch(DIGITS, chars[i]);
      if (idx < 0) {
        throw new IllegalArgumentException("YeinGid: 非法的 hexString 编码 \"" + hexString + "\"");
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
    long lowBits = (longs[12] & 0x1) << 63 | longs[13] << 58 | longs[14] << 53;

    int version = (int) (highBits >> 61 & VERSION_MASK);
    int timestamp = (int) (highBits >> 29 & SECONDS_MASK);
    int fid = (int) (highBits >> 12 & FID_MASK);

    // seq 有 12bits 是存储在高位中的
    int seq = (int) ((highBits & 0xfff) << 11 | (lowBits >> 53) & 0x7ff);
    return new YeinGid(version, timestamp, fid, seq, hexString);
  }

  /**
   * 返回 YeinGid 32进制编码的字符串.
   *
   * @return 32进制编码的字符串
   */
  public String toHexString() {
    if (hexString != null) {
      return hexString;
    }

    char[] value = new char[YEIN_GID_LENGTH];
    long highBits =
        (long) CURRENT_VERSION << 61 | (long) timestamp << 29 | (long) fid << 12 | (long) seq >> 11;
    // seq 剩余 11bits
    long lowBits = ((long) seq & 0x7ff) << 53;

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

    this.hexString = new String(value);
    return hexString;
  }

  @Override
  public String toString() {
    return toHexString();
  }
}
