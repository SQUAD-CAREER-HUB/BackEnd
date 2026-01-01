package org.squad.careerhub.domain.member;

import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;

public final class MemberFixture {

    public static Member createMember() {
        return Member.create(
                "email",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl"
        );
    }

}
