package org.squad.careerhub.domain.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewResult {

    WAITING ("대기"),
    PASS    ("합격"),
    FAIL    ("불합격");

    private final String description;

}