package org.squad.careerhub.domain.notification.enums;

public enum TriggerRule {
    DAYS_BEFORE_AT_TIME,  // N일 전 특정 시각 (ex: D-3 09:00)
    DAY_AT_TIME,          // 당일 특정 시각 (ex: D-day 09:00)
    HOURS_BEFORE          // N시간 전 (ex: 1시간 전)
}
