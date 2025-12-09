package org.squad.careerhub.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetType {
    APPLICATION("지원서"),
    TASK("지원서 기타 항목"),
    INTERVIEW("면접"),
    REVIEW("커뮤니티 리뷰"),
    OTHER("기타");

    private final String description;
}
