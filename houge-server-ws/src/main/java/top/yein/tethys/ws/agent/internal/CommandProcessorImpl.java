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
package top.yein.tethys.ws.agent.internal;

import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import top.yein.tethys.grpc.AgentPb;
import top.yein.tethys.ws.agent.CommandProcessor;
import top.yein.tethys.ws.agent.command.CommandHandler;

/**
 * Agent 命令处理器实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class CommandProcessorImpl implements CommandProcessor {

  private static final Logger log = LogManager.getLogger();
  private final Set<CommandHandler> commandHandlers;

  /**
   * 使用命令处理器集合构造对象.
   *
   * @param commandHandlers 命令处理器集
   */
  @Inject
  public CommandProcessorImpl(Set<CommandHandler> commandHandlers) {
    Objects.requireNonNull(commandHandlers);
    this.commandHandlers = commandHandlers;
  }

  @Override
  public void process(AgentPb.Command command) {
    log.debug("处理Agent命令 command={}", command);
    Flux.fromIterable(commandHandlers)
        .flatMap(handler -> handler.handle(command))
        .doOnError(ex -> log.error("处理命令错误 command={}", command, ex))
        .subscribe();
  }
}
