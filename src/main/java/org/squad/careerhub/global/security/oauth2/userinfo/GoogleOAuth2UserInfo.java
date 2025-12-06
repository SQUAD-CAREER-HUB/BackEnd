package org.squad.careerhub.global.security.oauth2.userinfo;

import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.squad.careerhub.domain.member.entity.SocialProvider;

@AllArgsConstructor
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return SocialProvider.GOOGLE.name();
    }

    @Override
    public String getProfileUrl() {
        return Optional.ofNullable(attribute.get("picture"))
                .map(Object::toString)
                .orElse("default_profile_image_url"); // TODO: 추후 기본 프로필 이미지 URL 설정
    }

    @Override
    public String getSocialId() {
        return Optional.ofNullable(attribute.get("sub"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2AuthenticationException("Google 사용자 ID를 찾을 수 없습니다."));
    }

    @Override
    public String getEmail() {
        return Optional.ofNullable(attribute.get("email"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2AuthenticationException("Google 이메일 정보를 찾을 수 없습니다. 이메일 제공 동의가 필요합니다."));
    }

    @Override
    public String getNickname() {
        return Optional.ofNullable(attribute.get("name"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2AuthenticationException("Google 닉네임 정보를 찾을 수 없습니다. 프로필 제공 동의가 필요합니다."));
    }

}

/*
- 구글의 유저 정보 Response JSON 예시 -
{
   "sub": "식별값",
   "name": "name",
   "given_name": "given_name",
   "picture": "https//lh3.googleusercontent.com/~~",
   "email": "email",
   "email_verified": true,
   "locale": "ko"
}
 */