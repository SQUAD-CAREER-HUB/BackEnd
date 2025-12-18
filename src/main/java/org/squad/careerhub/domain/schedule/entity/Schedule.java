package org.squad.careerhub.domain.schedule.entity;

import static java.util.Objects.requireNonNull;
import org.squad.careerhub.global.error.ErrorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.global.entity.BaseEntity;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.error.CareerHubException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "schedule"
)
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InterviewType interviewType;

    @Column(length = 100)
    private String interviewTypeDetail;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    @Column(name = "location")
    private String location;

    @Column(name = "link")
    private String link;


    @Enumerated(EnumType.STRING)
    @Column(name = "stage_status", nullable = false, length = 20)
    private StageStatus stageStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", length = 20)
    private SubmissionStatus submissionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", length = 20)
    private ApplicationStatus applicationStatus;


    public static Schedule etcCreate(
        Application application,
        StageType stageType,
        String stageName,
        LocalDateTime datetime,
        String location,
        String link,
        StageStatus stageStatus,
        SubmissionStatus submissionStatus,
        ApplicationStatus applicationStatus
    ) {
        Schedule s = new Schedule();
        s.application = requireNonNull(application);
        s.author = requireNonNull(application.getAuthor());
        s.stageType = requireNonNull(stageType);
        s.stageName = normalize(stageName);
        s.datetime = datetime;
        s.location = normalize(location);
        s.link = normalize(link);
        s.stageStatus = requireNonNull(stageStatus);

        // DOCUMENT만 submissionStatus 허용
        s.submissionStatus = (stageType == StageType.DOCUMENT) ? submissionStatus : null;
        // APPLICATION_CLOSE만 applicationStatus 허용
        s.applicationStatus =
            (stageType == StageType.APPLICATION_CLOSE)
                ? applicationStatus
                : null;

        s.validate();
        return s;
    }

    public static Schedule interviewCreate(
        Application application,
        StageType stageType,
        InterviewType interviewType,
        String interviewTypeDetail,
        LocalDateTime datetime,
        String location,
        String link,
        StageStatus stageStatus,
        ApplicationStatus applicationStatus
    ) {
        Schedule s = new Schedule();

        s.application = requireNonNull(application);
        s.author = requireNonNull(application.getAuthor());

        s.stageType = requireNonNull(stageType);
        s.interviewType = interviewType;
        s.interviewTypeDetail = normalize(interviewTypeDetail);

        s.datetime = requireNonNull(datetime);
        s.location = normalize(location);
        s.link = normalize(link);

        s.stageStatus = requireNonNull(stageStatus);

        s.applicationStatus = applicationStatus;

        s.validate();
        return s;
    }

    /**
     * 전체 교체(update는 항상 전체 값 전달한다는 정책 기준)
     */
    public void update(
        StageType stageType,
        String title,
        LocalDateTime datetime,
        String location,
        String link,
        StageStatus stageStatus,
        InterviewType interviewType,
        String interviewTypeDetail,
        SubmissionStatus submissionStatus,
        ApplicationStatus applicationStatus
    ) {
        this.stageType = requireNonNull(stageType);
        this.stageName = normalize(stageName);
        this.datetime = requireNonNull(datetime);
        this.location = normalize(location);
        this.link = normalize(link);
        this.interviewType = interviewType;
        this.interviewTypeDetail = interviewTypeDetail;
        this.stageStatus = requireNonNull(stageStatus);

        this.submissionStatus = (stageType == StageType.DOCUMENT) ? submissionStatus : null;
        this.applicationStatus = applicationStatus;


        validate();
    }

    private void validate() {
        requireNonNull(this.author);
        requireNonNull(this.application);
        requireNonNull(this.stageType);

        // author는 application.author와 동일해야 함
        if (this.application.getAuthor() != null
            && this.author.getId() != null
            && this.application.getAuthor().getId() != null
            && !this.author.getId().equals(this.application.getAuthor().getId())) {
            throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_AUTHOR_MISMATCH);
        }

        // DOCUMENT가 아니면 submissionStatus는 없어야 함
        if (this.stageType != StageType.DOCUMENT && this.submissionStatus != null) {
            throw new CareerHubException(ErrorStatus.INVALID_SUBMISSION_STATUS_RULE);
        }
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

}
