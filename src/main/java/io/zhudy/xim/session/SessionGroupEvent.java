package io.zhudy.xim.session;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public enum SessionGroupEvent {
  /** 订阅成功之前. */
  GROUP_SUB_BEFORE,
  /** 订阅成功之后. */
  GROUP_SUB_AFTER,
  /** 取消订阅成功之前. */
  GROUP_UNSUB_BEFORE,
  /** 取消订阅成功之后. */
  GROUP_UNSUB_AFTER,
}
