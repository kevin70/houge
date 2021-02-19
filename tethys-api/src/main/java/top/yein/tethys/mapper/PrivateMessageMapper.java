package top.yein.tethys.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.yein.tethys.dto.PrivateMessageDTO;
import top.yein.tethys.entity.PrivateMessage;

/**
 * 私聊消息对象映射器.
 *
 * @author KK (kzou227@qq.com)
 */
@Mapper
public interface PrivateMessageMapper {

  /** 映射器实例. */
  PrivateMessageMapper INSTANCE = Mappers.getMapper(PrivateMessageMapper.class);

  /**
   * 将{@link PrivateMessage}实体映射为{@link PrivateMessageDTO}.
   *
   * @param entity 实体
   * @return DTO
   */
  PrivateMessageDTO toPrivateMessageDTO(PrivateMessage entity);
}
