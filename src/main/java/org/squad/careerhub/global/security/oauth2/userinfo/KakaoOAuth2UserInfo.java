package org.squad.careerhub.global.security.oauth2.userinfo;

import java.util.Map;
import java.util.Optional;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.squad.careerhub.domain.member.entity.SocialProvider;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        validateAttributes(attributes);

        this.attributes = attributes;
        this.kakaoAccount = extractKakaoAccount(attributes);
        this.profile = extractProfile(kakaoAccount);
    }

    @Override
    public String getProvider() {
        return SocialProvider.KAKAO.name();
    }

    @Override
    public String getSocialId() {
        return  Optional.ofNullable(attributes.get("id"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2AuthenticationException("Kakao 사용자 ID를 찾을 수 없습니다."));
    }

    @Override
    public String getEmail() {
        return Optional.ofNullable(kakaoAccount.get("email"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2AuthenticationException("Kakao 이메일 정보를 찾을 수 없습니다. 이메일 제공 동의가 필요합니다."));
    }

    @Override
    public String getNickname() {
        return Optional.ofNullable(profile.get("nickname"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2AuthenticationException("Kakao 닉네임 정보를 찾을 수 없습니다. 프로필 제공 동의가 필요합니다."));
    }

    @Override
    public String getProfileUrl() {
        return Optional.ofNullable(profile.get("profile_image_url"))
                .map(Object::toString)
                .filter(url -> !url.isBlank())
                .orElse("default_profile_image_url"); // TODO: 추후 기본 프로필 이미지 URL 설정
    }

    private void validateAttributes(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new OAuth2AuthenticationException("Kakao 응답이 비어있습니다.");
        }
    }

    private Map<String, Object> extractKakaoAccount(Map<String, Object> attributes) {
        return Optional.ofNullable(attributes.get("kakao_account"))
                .filter(Map.class::isInstance)
                .map(obj -> (Map<String, Object>) obj)
                .orElseThrow(() -> new OAuth2AuthenticationException("Kakao 계정 정보를 찾을 수 없습니다."));
    }

    private Map<String, Object> extractProfile(Map<String, Object> kakaoAccount) {
        return Optional.ofNullable(kakaoAccount.get("profile"))
                .filter(Map.class::isInstance)
                .map(obj -> (Map<String, Object>) obj)
                .orElseThrow(() -> new OAuth2AuthenticationException("Kakao 프로필 정보를 찾을 수 없습니다."));
    }

}

/*
- 카카오의 유저 정보 Response JSON 예시 -
{
    "id":123456789,
    "connected_at": "2022-04-11T01:45:28Z",
    "kakao_account": {
        // 프로필 또는 닉네임 동의 항목 필요
        "profile_nickname_needs_agreement": false,
        // 프로필 또는 프로필 사진 동의 항목 필요
        "profile_image_needs_agreement	": false,
        "profile": {
            // 프로필 또는 닉네임 동의 항목 필요
            "nickname": "홍길동",
            // 프로필 또는 프로필 사진 동의 항목 필요
            "thumbnail_image_url": "http://yyy.kakao.com/.../img_110x110.jpg",
            "profile_image_url": "http://yyy.kakao.com/dn/.../img_640x640.jpg",
            "is_default_image":false
        },
        // 이름 동의 항목 필요
        "name_needs_agreement":false,
        "name":"홍길동",
        // 카카오계정(이메일) 동의 항목 필요
        "email_needs_agreement":false,
        "is_email_valid": true,
        "is_email_verified": true,
        "email": "sample@sample.com",
        // 연령대 동의 항목 필요
        "age_range_needs_agreement":false,
        "age_range":"20~29",
        // 출생 연도 동의 항목 필요
        "birthyear_needs_agreement": false,
        "birthyear": "2002",
        // 생일 동의 항목 필요
        "birthday_needs_agreement":false,
        "birthday":"1130",
        "birthday_type":"SOLAR",
        // 성별 동의 항목 필요
        "gender_needs_agreement":false,
        "gender":"female",
        // 카카오계정(전화번호) 동의 항목 필요
        "phone_number_needs_agreement": false,
        "phone_number": "+82 010-1234-5678",
        // CI(연계정보) 동의 항목 필요
        "ci_needs_agreement": false,
        "ci": "${CI}",
        "ci_authenticated_at": "2019-03-11T11:25:22Z",
    },
    "properties":{
        "${CUSTOM_PROPERTY_KEY}": "${CUSTOM_PROPERTY_VALUE}",
        ...
    }
}
 */