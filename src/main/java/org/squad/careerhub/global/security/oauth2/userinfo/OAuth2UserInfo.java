package org.squad.careerhub.global.security.oauth2.userinfo;

public interface OAuth2UserInfo {

    String getProvider();  //제공자 (Ex. naver, google, ...)
    String getProfileUrl();
    String getSocialId();   //제공자에서 발급해주는 아이디(번호)
    String getEmail();     //이메일
    String getNickname();      //사용자 이름 (설정한 이름)

}