package cool.houge.rest.controller.message;

import cool.houge.service.message.SendMessageInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/** @author KK (kzou227@qq.com) */
@Mapper
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
