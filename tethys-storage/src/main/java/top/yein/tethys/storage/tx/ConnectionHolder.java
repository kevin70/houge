package top.yein.tethys.storage.tx;

import io.r2dbc.spi.Connection;

/** @author KK (kzou227@qq.com) */
class ConnectionHolder {

  // 是否开启事务
  private volatile boolean isBeginTransaction;
  // 链接
  private final Connection connection;

  public ConnectionHolder(boolean isBeginTransaction, Connection connection) {
    this.isBeginTransaction = isBeginTransaction;
    this.connection = connection;
  }
}
