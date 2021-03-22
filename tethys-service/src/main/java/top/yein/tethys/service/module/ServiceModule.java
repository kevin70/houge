package top.yein.tethys.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import top.yein.tethys.service.UserService;

/**
 * 服务模块定义.
 *
 * @author KK (kzou227@qq.com)
 */
public class ServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(UserService.class).in(Scopes.SINGLETON);
  }
}
