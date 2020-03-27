package io.zhudy.xim.session;

import java.util.concurrent.ThreadLocalRandom;

/** @author Kevin Zou (kevinz@weghst.com) */
public class TestSession implements Session {

  private final long sessionId = ThreadLocalRandom.current().nextLong();

  @Override
  public long sessionId() {
    return sessionId;
  }

  @Override
  public String uid() {
    if (isAnonymous()) {
      return Long.toHexString(sessionId);
    }
    return authContext().uid();
  }
}
