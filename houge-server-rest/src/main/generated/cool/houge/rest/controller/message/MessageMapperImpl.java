package cool.houge.rest.controller.message;

import cool.houge.service.message.SendMessageInput;
import cool.houge.service.message.SendMessageInput.SendMessageInputBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-03T12:00:00+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
public class MessageMapperImpl implements MessageMapper {

    @Override
    public SendMessageInput mapToUser(SendMessageBody body, long uid) {

        SendMessageInputBuilder sendMessageInput = SendMessageInput.builder();

        if ( body != null ) {
            sendMessageInput.to( body.getUid() );
            sendMessageInput.content( body.getContent() );
            sendMessageInput.contentType( body.getContentType() );
            sendMessageInput.extraArgs( body.getExtraArgs() );
        }
        sendMessageInput.from( uid );

        return sendMessageInput.build();
    }

    @Override
    public SendMessageInput mapToGroup(SendMessageBody body, long uid) {

        SendMessageInputBuilder sendMessageInput = SendMessageInput.builder();

        if ( body != null ) {
            sendMessageInput.to( body.getGid() );
            sendMessageInput.content( body.getContent() );
            sendMessageInput.contentType( body.getContentType() );
            sendMessageInput.extraArgs( body.getExtraArgs() );
        }
        sendMessageInput.from( uid );

        return sendMessageInput.build();
    }
}
