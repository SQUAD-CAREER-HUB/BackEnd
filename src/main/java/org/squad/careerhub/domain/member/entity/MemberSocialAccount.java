package org.squad.careerhub.domain.member.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberSocialAccount {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialProvider provider;

    @Column(nullable = false)
    private String socialId;

    public static MemberSocialAccount create(
            String email,
            SocialProvider provider,
            String socialId
    ) {
        MemberSocialAccount socialAccount = new MemberSocialAccount();

        socialAccount.email = requireNonNull(email);
        socialAccount.provider = requireNonNull(provider);
        socialAccount.socialId = requireNonNull(socialId);

        return socialAccount;
    }

}