package cool.houge.rest.resource.inward.vo;

import cool.houge.service.GroupService.JoinMember;
import cool.houge.service.GroupService.JoinMember.JoinMemberBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-05-28T11:41:51+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
public class GroupMapperImpl implements GroupMapper {

    @Override
    public JoinMember mapToJoinGroup(JoinMemberGroupVo vo, long gid) {
        if ( vo == null ) {
            return null;
        }

        JoinMemberBuilder joinMember = JoinMember.builder();

        if ( vo != null ) {
            joinMember.uid( vo.getUid() );
        }
        joinMember.gid( gid );

        return joinMember.build();
    }
}
