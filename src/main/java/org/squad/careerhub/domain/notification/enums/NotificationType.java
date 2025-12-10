package org.squad.careerhub.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    INTERVIEW_REMINDER("면접 예정"),
    TASK_REMINDER("기타 예정"),
    DEADLINE_REMINDER("서류 마감 알림"),
    SYSTEM("시스템 알림"),
    COMMUNITY("커뮤니티 알림");

    private final String description;
}
