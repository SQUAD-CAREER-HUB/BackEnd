package org.squad.careerhub.domain.member.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Embedded
    private MemberSocialAccount socialAccount;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    public static Member create(
            String email,
            SocialProvider provider,
            String socialId,
            String nickname
    ) {
        Member member = new Member();

        member.nickname = requireNonNull(nickname);
        member.socialAccount = MemberSocialAccount.create(email, provider, socialId);
        member.role = Role.ROLE_MEMBER;
        member.refreshToken = null;

        return member;
    }

    // TODO: 프로필 수정 기능 구현 필요
    public void updateProfile() {

    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = requireNonNull(refreshToken);
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

}