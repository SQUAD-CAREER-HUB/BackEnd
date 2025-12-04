package org.squad.careerhub.domain.application.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationStatus {

    ALL                 ("전체 보기"),
    DOCUMENT_PREPARING  ("서류 준비 중"),
    DOCUMENT_SUBMITTED  ("서류 제출 완료"),
    DOCUMENT_PASSED     ("서류 통과"),
    WAITING_FOR_ETC     ("기타 전형 진행 중"),
    INTERVIEW_SCHEDULED ("면접 전형 중"),
    FINAL_PASSED        ("최종 합격"),
    REJECTED            ("불합격"),

    ;

    private final String description;

}