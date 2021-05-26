package cool.houge.rest.resource.inward.vo;

import cool.houge.service.GroupService.JoinMember;
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
  JoinMember mapToJoinGroup(JoinMemberGroupVo vo, long gid);
}
