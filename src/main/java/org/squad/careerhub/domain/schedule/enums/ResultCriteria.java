package org.squad.careerhub.domain.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCriteria {
    STAGE_PASS,     // 전형 합격
    FINAL_PASS,     // 최종 합격
    FINAL_FAIL      // 최종 불합격
}
