/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cool.houge.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.typesafe.config.Config;
import javax.inject.Singleton;
import cool.houge.ConfigKeys;
import cool.houge.service.GroupService;
import cool.houge.service.impl.GroupServiceImpl;
import cool.houge.service.MessageProps;
import cool.houge.service.MessageService;
import cool.houge.service.impl.MessageServiceImpl;
import cool.houge.service.MessageStorageService;
import cool.houge.service.impl.MessageStorageServiceImpl;
import cool.houge.service.UserService;
import cool.houge.service.impl.UserServiceImpl;
import cool.houge.storage.MessageDao;
import cool.houge.storage.query.MessageQueryDao;

/**
 * 服务模块定义.
 *
 * @author KK (kzou227@qq.com)
 */
public class ServiceModule extends AbstractModule {

  private final Config config;

  /**
   * 使用应用配置构建对象.
   *
   * @param config 应用配置
   */
  public ServiceModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(UserService.class).to(UserServiceImpl.class).in(Scopes.SINGLETON);
    bind(GroupService.class).to(GroupServiceImpl.class).in(Scopes.SINGLETON);
    bind(MessageStorageService.class).to(MessageStorageServiceImpl.class).in(Scopes.SINGLETON);

    // 安装 gRPC 存根模块
    install(new GrpcStubModule(config));
  }

  @Provides
  @Singleton
  public MessageService messageService(MessageDao messageDao, MessageQueryDao messageQueryDao) {
    var pullBeginTimeLimit = config.getDuration(ConfigKeys.MESSAGE_PULL_BEGIN_TIME_LIMIT);
    var props = MessageProps.builder().pullBeginTimeLimit(pullBeginTimeLimit).build();
    return new MessageServiceImpl(props, messageDao, messageQueryDao);
  }
}
