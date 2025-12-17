package org.squad.careerhub.domain.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewType {

    TECH      ("기술 면접"),
    FIT       ("컬처핏/인성 면접"),
    EXECUTIVE ("임원 면접"),
    DESIGN    ("시스템 디자인 면접"),
    TEST      ("라이브 코딩 테스트 면접"),
    ETC     ("기타");

    private final String description;

}