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
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 * 면접 일정 관리 Entity 자원서 생성 기능 구현을 위해 제가 면접 일정 엔티티를 추가했습니다. 변동 사항 있으시면 변경 부탁드립니다. from MunSu Kwak
 **/

@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_schedule_application_started_at", // 한 지원서에 대해 동일한 시작 일시의 일정은 하나만 존재할 수 있음
                columnNames = {"application_id", "started_at"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Schedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type", nullable = false)
    private StageType stageType;

    @Column(name = "stageName", length = 100)
    private String stageName;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", length = 20)
    private SubmissionStatus submissionStatus;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public static Schedule register(
            Member author,
            Application application,
            StageType stageType,
            String stageName,
            String location,
            SubmissionStatus submissionStatus,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        Schedule schedule = new Schedule();
        schedule.application = requireNonNull(application);
        schedule.author = requireNonNull(author);
        schedule.stageType = requireNonNull(stageType);
        schedule.stageName = requireNonNull(stageName);
        schedule.location = location;
        schedule.submissionStatus = submissionStatus; // nullable
        schedule.startedAt = requireNonNull(startedAt);
        schedule.endedAt = finishedAt;
        return schedule;
    }

}