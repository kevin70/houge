package io.zhudy.xim.session.impl;

import io.zhudy.xim.session.SessionIdGenerator;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认会话 ID 生成器实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class DefaultSessionIdGenerator implements SessionIdGenerator {

  private final AtomicInteger seq = new AtomicInteger();

  @Override
  public long nextId() {
    seq.compareAndSet(Integer.MAX_VALUE, 0);
    long t = Clock.systemDefaultZone().instant().getEpochSecond();
    return (t << 32) | seq.incrementAndGet();
  }
}
