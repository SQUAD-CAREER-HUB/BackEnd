package org.squad.careerhub.domain.schedule.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "schedule",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_schedule_stage_started_at",
                        columnNames = {"application_stage_id", "started_at"}
                ),
                @UniqueConstraint(
                        name = "uk_schedule_stage_schedule_name",
                        columnNames = {"application_stage_id", "schedule_name"}
                )
        }
)
@Entity
public class Schedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_stage_id", nullable = false)
    private ApplicationStage applicationStage;

    @Column(nullable = false)
    private String scheduleName;

    private String location; // 면접 전형일경우에만 값이 할당 됩니다

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleResult scheduleResult = ScheduleResult.WAITING;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus; // 서류 전형 일정일 경우에만 값이 할당 됩니다

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Schedule(
            Member author,
            ApplicationStage applicationStage,
            String scheduleName,
            String location,
            ScheduleResult scheduleResult,
            SubmissionStatus submissionStatus,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        this.author = requireNonNull(author);
        this.applicationStage = requireNonNull(applicationStage);
        this.startedAt = requireNonNull(startedAt);
        this.scheduleName = scheduleName;
        this.location = location;
        this.scheduleResult = scheduleResult;
        this.submissionStatus = submissionStatus;
        this.endedAt = endedAt;
    }

    // 정적 팩토리 메서드 - 의도를 명확히
    public static Schedule registerInterview(
            Member author,
            ApplicationStage applicationStage,
            String scheduleName,
            String location,
            ScheduleResult scheduleResult,
            LocalDateTime startedAt
    ) {
        // submissionStatus, endedAt은 null
        return Schedule.builder()
                .author(author)
                .applicationStage(applicationStage)
                .scheduleName(scheduleName)
                .location(location)
                .scheduleResult(scheduleResult)
                .startedAt(startedAt)
                .build();
    }

    public static Schedule registerDocs(
            Member author,
            ApplicationStage applicationStage,
            String scheduleName,
            SubmissionStatus submissionStatus,
            ScheduleResult scheduleResult,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        // location 은 null
        return Schedule.builder()
                .author(author)
                .applicationStage(applicationStage)
                .scheduleName(scheduleName)
                .submissionStatus(submissionStatus)
                .scheduleResult(scheduleResult)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }

    public static Schedule registerEtc(
            Member author,
            ApplicationStage applicationStage,
            String scheduleName,
            ScheduleResult scheduleResult,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        // location, submissionStatus 은 null
        return Schedule.builder()
                .author(author)
                .applicationStage(applicationStage)
                .scheduleName(scheduleName)
                .scheduleResult(scheduleResult)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }

    public void updateInterview(String scheduleName, LocalDateTime startedAt, String location,
            ScheduleResult result) {
        this.scheduleName = scheduleName;
        this.startedAt = startedAt;
        this.location = location;
        this.scheduleResult = result;
    }

    public void updateEtc(String scheduleName, LocalDateTime startedAt, LocalDateTime endedAt,
            ScheduleResult result) {
        this.scheduleName = scheduleName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.scheduleResult = result;
    }

}