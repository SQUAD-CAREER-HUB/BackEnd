package org.squad.careerhub.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class SocialProviderTest {

    @Test
    void 유효한_SocialProvider를_제공한다() {
        // when
        SocialProvider google = SocialProvider.from("GOOGLE");
        SocialProvider kakao = SocialProvider.from("kakao");

        // then
        assertThat(google).isEqualTo(SocialProvider.GOOGLE);
        assertThat(kakao).isEqualTo(SocialProvider.KAKAO);
    }

    @Test
    void 유효하지않은_SocialProvider를_제공할경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> SocialProvider.from("INVALID_PROVIDER"))
                .isInstanceOf(CareerHubException.class)
                .hasMessageContaining(ErrorStatus.UNSUPPORTED_OAUTH_PROVIDER.getMessage());
    }

}