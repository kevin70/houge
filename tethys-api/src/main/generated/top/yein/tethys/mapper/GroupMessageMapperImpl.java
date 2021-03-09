package top.yein.tethys.mapper;

import javax.annotation.processing.Generated;
import top.yein.tethys.dto.GroupMessageDTO;
import top.yein.tethys.entity.GroupMessage;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-09T15:23:12+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
public class GroupMessageMapperImpl implements GroupMessageMapper {

    @Override
    public GroupMessageDTO toGroupMessageDTO(GroupMessage entity) {
        if ( entity == null ) {
            return null;
        }

        GroupMessageDTO groupMessageDTO = new GroupMessageDTO();

        groupMessageDTO.setId( entity.getId() );
        groupMessageDTO.setGroupId( entity.getGroupId() );
        groupMessageDTO.setSenderId( entity.getSenderId() );
        groupMessageDTO.setKind( entity.getKind() );
        groupMessageDTO.setContent( entity.getContent() );
        groupMessageDTO.setUrl( entity.getUrl() );
        groupMessageDTO.setCustomArgs( entity.getCustomArgs() );
        groupMessageDTO.setCreateTime( entity.getCreateTime() );

        return groupMessageDTO;
    }
}
