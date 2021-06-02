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
package cool.houge.rest.controller.group;

import cool.houge.service.group.JoinMemberInput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 群组对象的 mapstruct 映射接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Mapper
public interface GroupMapper {

  GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

  /**
   * @param vo
   * @return
   */
  JoinMemberInput map(JoinMemberBody vo, long gid);
}
