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
public interface GroupBeanMapper {

  GroupBeanMapper INSTANCE = Mappers.getMapper(GroupBeanMapper.class);

  /**
   * @param vo
   * @return
   */
  JoinMemberInput map(JoinMemberData vo, long gid);
}
