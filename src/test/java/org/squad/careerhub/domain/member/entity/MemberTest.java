package org.squad.careerhub.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void Member_객체를_생성한다() {
        // given
        var email = "test@example.com";
        var provider = SocialProvider.GOOGLE;
        var socialId = "social123";
        var nickname = "testUser";
        var profileImageUrl = "http://example.com/profile.jpg";

        // when
        var member = Member.create(email, provider, socialId, nickname, profileImageUrl);

        // then
        assertThat(member).isNotNull()
                .extracting(
                        Member::getNickname,
                        Member::getRole,
                        Member::getRefreshToken
                ).containsExactly(
                        nickname,
                        Role.ROLE_MEMBER,
                        null
                );
        assertThat(member.getSocialAccount()).isNotNull()
                .extracting(
                        MemberSocialAccount::getEmail,
                        MemberSocialAccount::getProvider,
                        MemberSocialAccount::getSocialId
                ).containsExactly(
                        email,
                        provider,
                        socialId
                );
    }

    @Test
    void Member_생성시_닉네임이_NULL_이면_NullPointerException을_던진다() {
        // given
        var email = "test@example.com";
        var provider = SocialProvider.GOOGLE;
        var socialId = "social123";

        // when & then
        assertThatThrownBy(() -> Member.create(email, provider, socialId, null /* nickname */, "http://example.com/profile.jpg"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void refreshToken을_업데이트_한다() {
        // given
        var member = Member.create("test@example.com", SocialProvider.GOOGLE, "social123", "testUser", "http://example.com/profile.jpg");
        var newToken = "newRefreshToken";

        // when
        member.updateRefreshToken(newToken);

        // then
        assertThat(member.getRefreshToken()).isEqualTo(newToken);
    }

    @Test
    void RefreshToken_업데이트시_NULL_값이_들어가면_NullPointerException을_던진다() {
        // given
        var member = Member.create("test@example.com", SocialProvider.GOOGLE, "social123", "testUser", "http://example.com/profile.jpg");

        // when & then
        assertThatThrownBy(() -> member.updateRefreshToken(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void refreshToken을_null로_초기화_한다() {
        // given
        var member = Member.create("test@example.com", SocialProvider.GOOGLE, "social123", "testUser", "http://example.com/profile.jpg");
        member.updateRefreshToken("someToken");

        // when
        member.clearRefreshToken();

        // then
        assertThat(member.getRefreshToken()).isNull();
    }

}