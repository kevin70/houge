package io.zhudy.xim.session;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试专用的 Session.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class TestSession implements Session {

  private final long sessionId = ThreadLocalRandom.current().nextLong();
  private final Set<String> subGroupIds = new LinkedHashSet<>();

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

  @Override
  public Set<String> subGroupIds() {
    return subGroupIds;
  }
}
