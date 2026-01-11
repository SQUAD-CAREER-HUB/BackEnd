package org.squad.careerhub.domain.notification.enums;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationEvent {
    // ===== DEADLINE =====
    DEADLINE_D7("서류 마감 7일 전", NotificationType.DEADLINE_REMINDER, SourceType.APPLICATION,
            TriggerRule.DAYS_BEFORE_AT_TIME, 7, LocalTime.of(9, 0)),

    DEADLINE_D3("서류 마감 3일 전", NotificationType.DEADLINE_REMINDER, SourceType.APPLICATION,
            TriggerRule.DAYS_BEFORE_AT_TIME, 3, LocalTime.of(9, 0)),

    DEADLINE_D1("서류 마감 1일 전", NotificationType.DEADLINE_REMINDER, SourceType.APPLICATION,
            TriggerRule.DAYS_BEFORE_AT_TIME, 1, LocalTime.of(9, 0)),

    // ===== INTERVIEW =====
    INTERVIEW_D3("면접 3일 전", NotificationType.INTERVIEW_REMINDER, SourceType.INTERVIEW,
            TriggerRule.DAYS_BEFORE_AT_TIME, 3, LocalTime.of(9, 0)),

    INTERVIEW_D1("면접 1일 전", NotificationType.INTERVIEW_REMINDER, SourceType.INTERVIEW,
            TriggerRule.DAYS_BEFORE_AT_TIME, 1, LocalTime.of(9, 0)),

    INTERVIEW_DAY_9AM("면접 당일 오전 9시", NotificationType.INTERVIEW_REMINDER, SourceType.INTERVIEW,
            TriggerRule.DAY_AT_TIME, 0, LocalTime.of(9, 0)),

    INTERVIEW_1H_BEFORE("면접 1시간 전", NotificationType.INTERVIEW_REMINDER, SourceType.INTERVIEW,
            TriggerRule.HOURS_BEFORE, 1, null),

    // ===== ETC/TASK =====
    ETC_D3("기타 일정 3일 전", NotificationType.TASK_REMINDER, SourceType.TASK,
            TriggerRule.DAYS_BEFORE_AT_TIME, 3, LocalTime.of(9, 0)),

    ETC_D1("기타 일정 1일 전", NotificationType.TASK_REMINDER, SourceType.TASK,
            TriggerRule.DAYS_BEFORE_AT_TIME, 1, LocalTime.of(9, 0)),

    ETC_DAY_9AM("기타 일정 당일 오전 9시", NotificationType.TASK_REMINDER, SourceType.TASK,
            TriggerRule.DAY_AT_TIME, 0, LocalTime.of(9, 0));

    private final String description;
    private final NotificationType notificationType;
    private final SourceType sourceType;

    private final TriggerRule triggerRule;
    private final int amount;          // daysBefore or hoursBefore (rule에 따라 의미가 달라짐)
    private final LocalTime atTime;    // rule이 *_AT_TIME 일 때 사용

    /**
     * 기준 일정(마감/면접/기타 일정 시각)으로부터 "이 이벤트 알림이 발송되어야 하는 시각"을 계산
     */
    public LocalDateTime computeFireAt(LocalDateTime baseDateTime) {
        return switch (triggerRule) {
            case DAYS_BEFORE_AT_TIME -> baseDateTime.toLocalDate()
                    .minusDays(amount)
                    .atTime(atTime);

            case DAY_AT_TIME -> baseDateTime.toLocalDate()
                    .atTime(atTime);

            case HOURS_BEFORE -> baseDateTime.minusHours(amount);
        };
    }
}
