package org.squad.careerhub.domain.application.entity;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StageType {

    DOCUMENT   (1, "서류 전형"),
    ETC        (2, "기타 전형"),
    INTERVIEW  (3, "면접 전형"),
    FINAL_PASS (4, "최종 합격"),
    FINAL_FAIL (4, "최종 불합격"),
    ;

    private final int order;
    private final String description;

    public boolean isFinalStage() {
        return this == FINAL_PASS || this == FINAL_FAIL;
    }

}