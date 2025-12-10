package org.squad.careerhub.domain.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewStatus {
    SCHEDULED("예정"),
    DONE("진행 완료"),
    CANCELED("취소"),
    NONAPPEARANCE("불참");

    private final String description;
}
