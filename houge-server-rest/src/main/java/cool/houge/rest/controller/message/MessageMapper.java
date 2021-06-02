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
package cool.houge.rest.controller.message;

import cool.houge.service.message.SendMessageInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/** @author KK (kzou227@qq.com) */
@Mapper(
  nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface MessageMapper {

  MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

  @Mappings({
    @Mapping(source = "uid", target = "from"),
    @Mapping(source = "body.uid", target = "to")
  })
  SendMessageInput mapToUser(SendMessageBody body, long uid);

  @Mappings({
    @Mapping(source = "uid", target = "from"),
    @Mapping(source = "body.gid", target = "to")
  })
  SendMessageInput mapToGroup(SendMessageBody body, long uid);
}
