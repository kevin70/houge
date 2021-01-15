package top.yein.tethys.util;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** @author KK (kzou227@qq.com) */
public class IdTest {

  String id = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

  @Test
  void exec() {
    // 时间：秒，支持到 2106 年
    var maxSecs = (long) Math.pow(2, 32) - 1;
    System.out.println(maxSecs);
    System.out.println(Instant.ofEpochSecond(maxSecs));

    // 自旋ID：1048575
    var seqId = (long) Math.pow(2, 16) - 1;
    System.out.println(seqId);

    // 随机数
    var ran = (long) Math.pow(2, 4) - 1;
    System.out.println(ran);

    // 节点ID：65535
    var nodeId = (long) Math.pow(2, 16) - 1;
    System.out.println(nodeId);

    // zoneId
    var zoneId = (long) Math.pow(2, 8) - 1;
    System.out.println(zoneId);

    System.out.println(UUID.randomUUID());
    // 36bit + 20bit + 16bit + 8bit = 80bit
    System.out.println(Math.pow(2, 5));
  }
}
