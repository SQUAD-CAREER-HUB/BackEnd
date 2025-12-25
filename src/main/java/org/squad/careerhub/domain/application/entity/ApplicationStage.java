package org.squad.careerhub.domain.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ApplicationStage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageType stageType;

    private String stageName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageStatus stageStatus;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus; // 서류 전형일 경우에만 값이 할당 됩니다

    public static ApplicationStage create(
            Application application,
            StageType stageType,
            String stageName,
            SubmissionStatus submissionStatus
    ) {
        return new ApplicationStage(
                application,
                stageType,
                stageName,
                StageStatus.WAITING,
                stageType == StageType.DOCUMENT ? submissionStatus : null
        );
    }

    public static ApplicationStage createPassedDocumentStage(Application application) {
        return new ApplicationStage(
                application,
                StageType.DOCUMENT,
                StageType.DOCUMENT.getDescription(),
                StageStatus.PASS,
                SubmissionStatus.SUBMITTED
        );
    }

    // test용 임시 전형 상태 업데이트 메서드 (예: PASS, FAIL 등)
    public void updateStageStatus(StageStatus newStatus) {
        this.stageStatus = newStatus;
    }

}