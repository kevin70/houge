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
package top.yein.tethys.core.id;

import javax.inject.Inject;
import reactor.core.publisher.Flux;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.util.YeinGid;

/**
 * YeinGid 消息 ID 生成器实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class YeinGidMessageIdGenerator implements MessageIdGenerator {

  private final ApplicationIdentifier applicationIdentifier;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param applicationIdentifier 应用标志对象
   */
  @Inject
  public YeinGidMessageIdGenerator(ApplicationIdentifier applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @Override
  public String nextId() {
    return new YeinGid(applicationIdentifier.fid()).toHexString();
  }

  @Override
  public Flux<String> nextIds() {
    return Flux.create(
        sink -> {
          long limit = sink.requestedFromDownstream();
          if (limit > REQUEST_IDS_LIMIT) {
            limit = REQUEST_IDS_LIMIT;
          }

          for (int i = 0; i < limit; i++) {
            sink.next(new YeinGid(applicationIdentifier.fid()).toHexString());
          }
          sink.complete();
        });
  }
}
