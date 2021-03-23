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
package top.yein.tethys.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import top.yein.tethys.service.GroupService;
import top.yein.tethys.service.GroupServiceImpl;
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
    bind(GroupService.class).to(GroupServiceImpl.class).in(Scopes.SINGLETON);
  }
}
