package org.squad.careerhub.domain.application.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationMethod {

    HOMEPAGE ("홈페이지 지원"),
    EMAIL    ("이메일"),
    PLATFORM ("채용 플랫폼"),
    REFERRAL ("지인 추천");

    private final String description;

}