package org.squad.careerhub.domain.application.entity;

import static java.util.Objects.requireNonNull;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

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
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.entity.BaseEntity;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

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
    private LocalDateTime deadline;

    @Column(length = 5000)
    private String memo;

    public static Application create(
            Member author,
            String jobPostingUrl,
            String company,
            String position,
            String jobLocation,
            StageType currentStageType,
            ApplicationStatus applicationStatus,
            ApplicationMethod applicationMethod,
            LocalDateTime deadline
    ) {
        ApplicationStatus currentApplicationStatus = currentStageType == StageType.APPLICATION_CLOSE ?
                applicationStatus : ApplicationStatus.IN_PROGRESS;

        Application application = new Application();

        application.author = author;
        application.jobPostingUrl = jobPostingUrl;
        application.company = requireNonNull(company);
        application.position = requireNonNull(position);
        application.jobLocation = jobLocation;
        application.currentStageType = requireNonNull(currentStageType);
        application.applicationStatus = currentApplicationStatus;
        application.applicationMethod = requireNonNull(applicationMethod);
        application.deadline = requireNonNull(deadline);
        application.memo = null;

        return application;
    }

    public boolean isDeadlinePassed() {
        return now().isAfter(this.deadline);
    }

    public void update(
            String jobPostingUrl,
            String company,
            String position,
            String jobLocation,
            String memo
    ) {
        this.jobPostingUrl = jobPostingUrl;
        this.company = requireNonNull(company);
        this.position = requireNonNull(position);
        this.jobLocation = jobLocation;
        this.memo = memo;

    }

    public void updateCurrentStageType(StageType currentStageType) {
        this.currentStageType = currentStageType;
    }

    public void validateOwnedBy(Long memberId) {
        if (this.author == null || this.author.getId() == null) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }
        if (!this.author.getId().equals(memberId)) {
            throw new CareerHubException(ErrorStatus.FORBIDDEN_ERROR);
        }
    }

    // Test를 위한 업데이트 메서드
    public void updateApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

}