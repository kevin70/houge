package top.yein.tethys.storage.query;

import org.junit.jupiter.api.Test;
import top.yein.tethys.storage.AbstractTestDao;

/**
 * {@link MessageQueryDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageQueryDaoImplTest extends AbstractTestDao {

  MessageQueryDao newMessageQueryDao() {
    return new MessageQueryDaoImpl(r2dbcClient);
  }

  @Test
  void queryById() {
    var dao = newMessageQueryDao();
  }

  @Test
  void queryByUser() {

  }
}
