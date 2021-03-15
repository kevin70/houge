package top.yein.tethys.repository;

import com.google.inject.AbstractModule;

/**
 * @author KK (kzou227@qq.com)
 */
public class RepositoryModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MessageRepository.class).to(MessageRepositoryImpl.class);
  }
}
