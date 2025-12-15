package org.squad.careerhub.domain.schedule.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 * 면접 일정 관리 Entity
 * 자원서 생성 기능 구현을 위해 제가 면접 일정 엔티티를 추가했습니다.
 * 변동 사항 있으시면 변경 부탁드립니다.
 * from MunSu Kwak
 **/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InterviewSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    public static InterviewSchedule register(
            Application application,
            String title,
            InterviewType type,
            String location,
            LocalDateTime scheduledAt
    ) {
        InterviewSchedule interviewSchedule = new InterviewSchedule();
        interviewSchedule.application = requireNonNull(application);
        interviewSchedule.title = requireNonNull(title);
        interviewSchedule.type = requireNonNull(type);
        interviewSchedule.location = requireNonNull(location);
        interviewSchedule.scheduledAt = requireNonNull(scheduledAt);
        return interviewSchedule;
    }

    public void updateSchedule(
            String title,
            InterviewType type,
            String location,
            LocalDateTime scheduledAt
    ) {
        this.title = requireNonNull(title);
        this.type = requireNonNull(type);
        this.location = requireNonNull(location);
        this.scheduledAt = requireNonNull(scheduledAt);
    }

    // 현재 기준 면접 일자가 지났는지 여부 반환
    public boolean isPast() {
        return this.scheduledAt.isBefore(LocalDateTime.now());
    }

}