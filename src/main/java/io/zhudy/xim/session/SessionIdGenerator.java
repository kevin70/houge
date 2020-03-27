package io.zhudy.xim.session;

/**
 * 会话 ID 生成器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionIdGenerator {

  /**
   * 返回下一个会话 ID.
   *
   * @return 会话 ID
   */
  long nextId();
}
