package cool.houge.rest.controller.group;

import cool.houge.service.group.JoinMemberInput;
import cool.houge.service.group.JoinMemberInput.JoinMemberInputBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-03T12:00:00+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
public class GroupMapperImpl implements GroupMapper {

    @Override
    public JoinMemberInput map(JoinMemberBody vo, long gid) {
        if ( vo == null ) {
            return null;
        }

        JoinMemberInputBuilder joinMemberInput = JoinMemberInput.builder();

        if ( vo != null ) {
            joinMemberInput.uid( vo.getUid() );
        }
        joinMemberInput.gid( gid );

        return joinMemberInput.build();
    }
}
