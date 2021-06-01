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
