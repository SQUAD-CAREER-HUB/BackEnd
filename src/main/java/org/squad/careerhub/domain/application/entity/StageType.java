package org.squad.careerhub.domain.application.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StageType {

    DOCUMENT          (1, "서류 전형"),
    ETC               (2, "기타 전형"),
    INTERVIEW         (3, "면접 전형"),
    APPLICATION_CLOSE (4, "지원 종료"),

    ;

    private final int order;
    private final String description;

    public boolean isApplicationClose() {
        return this == APPLICATION_CLOSE;
    }

    public  boolean isDocument() {
        return this == DOCUMENT;
    }

    public boolean isEtc() {
        return this == ETC;
    }

    public boolean isInterview() {
        return this == INTERVIEW;
    }

}