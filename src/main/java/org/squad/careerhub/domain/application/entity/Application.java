package org.squad.careerhub.domain.application.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    private String jobPostingUrl;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String position;

    private String jobLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StageType currentStageType;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationMethod applicationMethod;

    @Column(nullable = false)
    private LocalDate deadline;

    private LocalDate submittedAt;

    @Column(length = 5000)
    private String memo;

    public static Application create(
            Member author,
            String jobPostingUrl,
            String company,
            String position,
            String jobLocation,
            StageType currentStageType,
            ApplicationMethod applicationMethod,
            LocalDate deadline,
            LocalDate submittedAt
    ) {
        Application application = new Application();

        application.author = author;
        application.jobPostingUrl = jobPostingUrl;
        application.company = requireNonNull(company);
        application.position = requireNonNull(position);
        application.jobLocation = requireNonNull(jobLocation);
        application.currentStageType = requireNonNull(currentStageType);
        application.applicationStatus = currentStageType != StageType.APPLICATION_CLOSE ? ApplicationStatus.IN_PROGRESS : null;
        application.applicationMethod = requireNonNull(applicationMethod);
        application.deadline = requireNonNull(deadline);
        application.submittedAt = submittedAt;
        application.memo = null;

        return application;
    }

    public void updateApplication(
            String jobPostingUrl,
            String company,
            String position,
            String jobLocation,
            ApplicationMethod applicationMethod,
            LocalDate deadline,
            LocalDate submittedAt,
            String memo
    ) {
        this.jobPostingUrl = jobPostingUrl;
        this.company = requireNonNull(company);
        this.position = requireNonNull(position);
        this.jobLocation = requireNonNull(jobLocation);
        this.applicationMethod = requireNonNull(applicationMethod);
        this.deadline = requireNonNull(deadline);
        this.submittedAt = submittedAt;
        this.memo = memo;
    }

    public boolean isDeadlinePassed() {
        return LocalDate.now().isAfter(this.deadline);
    }

    // Test를 위한 업데이트 메서드
    public void updateApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

}