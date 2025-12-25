package org.squad.careerhub.domain.schedule.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.global.entity.BaseEntity;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "schedule",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_stage_id_schedule_name", columnNames = {"stage_id", "schedule_name"}),
        @UniqueConstraint(name = "uk_stage_id_started_at", columnNames = {"stage_id", "started_at"})
    }
)
public class Schedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private ApplicationStage stage;

    @Column(name = "schedule_name", nullable = false, length = 100)
    private String scheduleName;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_result", nullable = false, length = 20)
    private StageStatus scheduleResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", length = 20)
    private SubmissionStatus submissionStatus;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    private String link;

    public static Schedule createEtc(
        ApplicationStage stage,
        String scheduleName,
        LocalDateTime startedAt,
        LocalDateTime endedAt
    ) {
        requireNonNull(stage);

        if (stage.getStageType() != StageType.ETC) {
            throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
        }

        Schedule s = new Schedule();
        s.stage = stage;
        s.author = requireNonNull(stage.getApplication().getAuthor());

        s.scheduleName = requireNonNull(normalize(scheduleName));
        s.startedAt = requireNonNull(startedAt);
        s.endedAt = endedAt;
        // ETC 생성 규칙: location 없음
        s.location = null;
        s.submissionStatus = null;
        s.scheduleResult = StageStatus.WAITING;
        return s;
    }

    public static Schedule register(
        ApplicationStage stage,
        String scheduleName,
        String location,
        SubmissionStatus submissionStatus,
        LocalDateTime startedAt,
        LocalDateTime endedAt
    ) {
        requireNonNull(stage);

        // ETC / INTERVIEW만 지원 (원하면 DOCUMENT도 확장 가능)
        if (stage.getStageType() == StageType.ETC) {
            return createEtc(
                stage,
                scheduleName,
                startedAt,
                endedAt
            );
        }

        if (stage.getStageType() == StageType.INTERVIEW) {
            return createInterview(
                stage,
                scheduleName,
                startedAt,
                location
            );
        }

        throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
    }

    public static Schedule createInterview(
        ApplicationStage stage,
        String scheduleName,
        LocalDateTime startedAt,
        String location
    ) {
        requireNonNull(stage);

        if (stage.getStageType() != StageType.INTERVIEW) {
            throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
        }

        Schedule s = new Schedule();
        s.stage = stage;
        s.author = requireNonNull(stage.getApplication().getAuthor());
        s.scheduleName = requireNonNull(normalize(scheduleName));
        s.startedAt = requireNonNull(startedAt);
        // INTERVIEW 생성 규칙: endedAt 없음
        s.endedAt = null;
        s.location = normalize(location);
        s.submissionStatus = null;
        s.scheduleResult = StageStatus.WAITING;

        return s;
    }

    public void update(
        String scheduleName,
        String location,
        StageStatus scheduleResult,
        SubmissionStatus submissionStatus,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String link
    ) {
        this.scheduleName = requireNonNull(normalize(scheduleName));
        this.location = normalize(location);
        this.scheduleResult = requireNonNull(scheduleResult);
        this.submissionStatus = submissionStatus;
        this.startedAt = requireNonNull(startedAt);
        this.endedAt = endedAt;
        this.link = normalize(link);

        validate();
    }

    private void validate() {
        // author는 stage.application.author와 동일해야 함
        Application app = stage.getApplication();
        if (app == null || app.getAuthor() == null) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }
        if (author.getId() != null && app.getAuthor().getId() != null
            && !author.getId().equals(app.getAuthor().getId())) {
            throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_AUTHOR_MISMATCH);
        }

        // DOCUMENT가 아니면 submissionStatus는 null이어야 함
        StageType stageType = stage.getStageType();
        if (stageType != StageType.DOCUMENT && submissionStatus != null) {
            throw new CareerHubException(ErrorStatus.INVALID_SUBMISSION_STATUS_RULE);
        }
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
