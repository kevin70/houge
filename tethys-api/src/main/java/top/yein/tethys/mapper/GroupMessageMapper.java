package top.yein.tethys.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.yein.tethys.dto.GroupMessageDTO;
import top.yein.tethys.entity.GroupMessage;

/**
 * 群组消息对象映射器.
 *
 * @author KK (kzou227@qq.com)
 */
@Mapper
public interface GroupMessageMapper {

  /** 映射器实例. */
  GroupMessageMapper INSTANCE = Mappers.getMapper(GroupMessageMapper.class);

  /**
   * 将{@link GroupMessage}实体映射为{@link GroupMessageDTO}.
   *
   * @param entity 实体
   * @return DTO
   */
  GroupMessageDTO toGroupMessageDTO(GroupMessage entity);
}
