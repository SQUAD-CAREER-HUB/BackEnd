package org.squad.careerhub.domain.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewType {

    TECH("기술 면접"),
    FIT("컬처핏 면접"),
    EXECUTIVE("임원 면접"),
    TASK("과제 전형"),
    TEST("시험/코딩테스트"),
    OTHER("기타");

    private final String description;
}