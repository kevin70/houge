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
package cool.houge.service.message.impl;

import cool.houge.grpc.MessageGrpc.MessageStub;
import cool.houge.grpc.MessagePb.SendMessageRequest;
import cool.houge.service.message.SendMessageInput;
import cool.houge.service.message.SendMessageService;
import reactor.core.publisher.Mono;

/** @author KK (kzou227@qq.com) */
public class SendMessageServiceImpl implements SendMessageService {

  private final MessageStub messageStub;

  public SendMessageServiceImpl(MessageStub messageStub) {
    this.messageStub = messageStub;
  }

  @Override
  public Mono<Void> sendToUser(SendMessageInput input) {
    var request = SendMessageRequest.newBuilder();
    return null;
  }

  @Override
  public Mono<Void> sendToGroup(SendMessageInput input) {
    return null;
  }
}
