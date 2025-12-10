package org.squad.careerhub.global.security.oauth2.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.service.MemberManager;
import org.squad.careerhub.domain.member.service.MemberReader;
import org.squad.careerhub.global.security.oauth2.dto.CustomOAuth2Member;
import org.squad.careerhub.global.security.oauth2.userinfo.OAuth2UserInfo;
import org.squad.careerhub.global.security.oauth2.userinfo.OAuth2UserInfoFactory;


/**
 * OAuth2 로그인 후 사용자 정보를 처리하는 서비스
 * Spring Security의 DefaultOAuth2UserService를 확장하여 OAuth2 제공자로부터 받은 사용자 정보를 처리하고
 * 데이터베이스에 저장하거나 업데이트합니다.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberReader memberReader;
    private final MemberManager memberManager;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        try {
            return authenticateOAuth2User(request, oAuth2User);
        } catch (OAuth2AuthenticationException ex) {
            log.warn("[Auth] OAuth2 인증 실패: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.warn("[Auth] OAuth2 사용자 정보 처리 중 예외 발생: {} ", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException("소셜 로그인 처리 중 오류가 발생했습니다.");
        }
    }

    private OAuth2User authenticateOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // OAuth2 제공자 타입 추출 (google, kakao 등)
        String providerName = extractProviderName(userRequest);
        SocialProvider provider = SocialProvider.from(providerName);

        // 제공자별 사용자 정보 파싱
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(provider, oauth2User.getAttributes());

        Member member = findOrCreateMember(userInfo, provider);

        return new CustomOAuth2Member(member, oauth2User.getAttributes());
    }

    private String extractProviderName(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId();
    }

    private Member findOrCreateMember(OAuth2UserInfo userInfo, SocialProvider provider) {
        Optional<Member> optionalMember = memberReader.findMemberBySocial(provider, userInfo.getSocialId());

        return optionalMember.orElseGet(() -> memberManager.create(
                Member.create(
                        userInfo.getEmail(),
                        provider,
                        userInfo.getSocialId(),
                        userInfo.getNickname(),
                        userInfo.getProfileUrl()
                )
        ));
    }

}