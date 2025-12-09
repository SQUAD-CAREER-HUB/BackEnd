package org.squad.careerhub.global.security.oauth2.userinfo;

import java.util.Map;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.member.entity.SocialProvider;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class OAuth2UserInfoFactory {

    /**
     * OAuth2 제공자에 따라 적절한 UserInfo 객체 생성
     *
     * @param provider OAuth2 제공자 타입
     * @param attributes OAuth2 제공자가 반환한 사용자 정보 Map
     * @return 파싱된 OAuth2UserInfo 객체
     **/

    public static OAuth2UserInfo create(
            SocialProvider provider,
            Map<String, Object> attributes
    ) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
        };
    }

}