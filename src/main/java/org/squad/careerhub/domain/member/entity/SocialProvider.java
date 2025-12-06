package org.squad.careerhub.domain.member.entity;

import org.squad.careerhub.global.error.CareerHubException;

public enum SocialProvider {

    GOOGLE,
    KAKAO,

    ;

    public static SocialProvider from(String type) {
        for (SocialProvider provider : values()) {
            if (provider.name().equalsIgnoreCase(type)) {
                return provider;
            }
        }
        throw new CareerHubException("Unsupported OAuth type: " + type);
    }

}