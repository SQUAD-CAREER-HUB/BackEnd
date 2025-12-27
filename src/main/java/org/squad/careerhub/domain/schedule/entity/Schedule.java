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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

    public static Schedule register(
            Member author,
            ApplicationStage applicationStage,
            String scheduleName,
            String location,
            ScheduleResult scheduleResult,
            SubmissionStatus submissionStatus,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        Schedule schedule = new Schedule();
        schedule.author = requireNonNull(author);
        schedule.applicationStage = requireNonNull(applicationStage);
        schedule.scheduleName = scheduleName;
        schedule.scheduleResult = scheduleResult;
        schedule.submissionStatus = submissionStatus;
        schedule.location = location;
        schedule.startedAt = requireNonNull(startedAt);
        schedule.endedAt = endedAt;
        return schedule;
    }

    public void update(
        String scheduleName,
        String location,
        ScheduleResult scheduleResult,
        SubmissionStatus submissionStatus,
        LocalDateTime startedAt,
        LocalDateTime endedAt
    ) {
        this.scheduleName = requireNonNull(scheduleName);
        this.location = location;
        this.scheduleResult = requireNonNull(scheduleResult);
        this.submissionStatus = submissionStatus;
        this.startedAt = requireNonNull(startedAt);
        this.endedAt = endedAt;
    }
}