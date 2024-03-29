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
package cool.houge.logic.agent;

import io.grpc.stub.ServerCallStreamObserver;
import cool.houge.grpc.AgentPb;

/** @author KK (kzou227@qq.com) */
class LinkResponseHolder {

  String name;
  String hostName;
  ServerCallStreamObserver<AgentPb.LinkResponse> observer;

  public LinkResponseHolder(
      String name, String hostName, ServerCallStreamObserver<AgentPb.LinkResponse> observer) {
    this.name = name;
    this.hostName = hostName;
    this.observer = observer;
  }
}
