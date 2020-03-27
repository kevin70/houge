package io.zhudy.xim.session;

import io.zhudy.xim.auth.AuthContext;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

/** @author Kevin Zou (kevinz@weghst.com) */
public class TestAuthContext implements AuthContext {

  final String uid = Long.toHexString(ThreadLocalRandom.current().nextLong());

  @Override
  public String uid() {
    return uid;
  }

  @Nonnull
  @Override
  public String token() {
    return uid;
  }

  @Override
  public boolean isAnonymous() {
    return false;
  }
}
