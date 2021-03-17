package top.yein.tethys.mapper;

import javax.annotation.processing.Generated;
import top.yein.tethys.dto.PrivateMessageDTO;
import top.yein.tethys.entity.PrivateMessage;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-17T10:11:47+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
public class PrivateMessageMapperImpl implements PrivateMessageMapper {

    @Override
    public PrivateMessageDTO toPrivateMessageDTO(PrivateMessage entity) {
        if ( entity == null ) {
            return null;
        }

        PrivateMessageDTO privateMessageDTO = new PrivateMessageDTO();

        privateMessageDTO.setId( entity.getId() );
        privateMessageDTO.setSenderId( entity.getSenderId() );
        privateMessageDTO.setReceiverId( entity.getReceiverId() );
        privateMessageDTO.setKind( entity.getKind() );
        privateMessageDTO.setContent( entity.getContent() );
        privateMessageDTO.setUrl( entity.getUrl() );
        privateMessageDTO.setCustomArgs( entity.getCustomArgs() );
        privateMessageDTO.setUnread( entity.getUnread() );
        privateMessageDTO.setCreateTime( entity.getCreateTime() );

        return privateMessageDTO;
    }
}
