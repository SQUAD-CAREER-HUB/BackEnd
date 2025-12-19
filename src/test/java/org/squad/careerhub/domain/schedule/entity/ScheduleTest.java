package org.squad.careerhub.domain.schedule.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

class ScheduleTest {

    Member author;
    Application application;

    @BeforeEach
    void setUp() {
        author = Member.create("email@gmail.com", SocialProvider.KAKAO, "socialId", "nickname", "profileImageUrl");

        application = Application.create(
            author,
            "http://jobposting.url",
            "CompanyName",
            "PositionName",
            "JobLocation",
            StageType.DOCUMENT,
            ApplicationMethod.EMAIL,
            LocalDate.of(2024, 12, 31),
            LocalDate.of(2024, 10, 1)
        );
    }

    @Test
    void 기타일정을_생성한다_ETC() {
        // when
        Schedule schedule = Schedule.etcCreate(
            application,
            StageType.ETC,
            "  코딩테스트  ",
            LocalDateTime.of(2025, 12, 5, 23, 59),
            "  온라인  ",
            "  https://...  ",
            StageStatus.WAITING,
            SubmissionStatus.SUBMITTED, // ETC이므로 무시되어야 함
            ApplicationStatus.IN_PROGRESS // ETC이므로 null로 들어가야 함
        );

        // then
        assertThat(schedule).extracting(
            Schedule::getAuthor,
            Schedule::getApplication,
            Schedule::getStageType,
            Schedule::getStageName,
            Schedule::getDatetime,
            Schedule::getLocation,
            Schedule::getLink,
            Schedule::getStageStatus,
            Schedule::getSubmissionStatus,
            Schedule::getApplicationStatus
        ).containsExactly(
            author,
            application,
            StageType.ETC,
            "코딩테스트", // normalize(trim)
            LocalDateTime.of(2025, 12, 5, 23, 59),
            "온라인",
            "https://...",
            StageStatus.WAITING,
            null, // DOCUMENT만 허용
            null  // APPLICATION_CLOSE만 허용
        );
    }

    @Test
    void 서류일정은_submissionStatus를_가질_수_있다_DOCUMENT() {
        // when
        Schedule schedule = Schedule.etcCreate(
            application,
            StageType.DOCUMENT,
            "서류 마감",
            LocalDateTime.of(2025, 3, 25, 23, 59),
            null,
            null,
            StageStatus.WAITING,
            SubmissionStatus.NOT_SUBMITTED,
            ApplicationStatus.IN_PROGRESS
        );

        // then
        assertThat(schedule.getStageType()).isEqualTo(StageType.DOCUMENT);
        assertThat(schedule.getSubmissionStatus()).isEqualTo(SubmissionStatus.NOT_SUBMITTED);
        // DOCUMENT는 applicationStatus 강제 null (현재 구현 기준)
        assertThat(schedule.getApplicationStatus()).isNull();
    }

    @Test
    void DOCUMENT가_아닌데_submissionStatus를_줘도_null로_저장된다() {
        Schedule schedule = Schedule.etcCreate(
            application,
            StageType.ETC,
            "코딩테스트",
            LocalDateTime.of(2025, 12, 5, 23, 59),
            null,
            null,
            StageStatus.WAITING,
            SubmissionStatus.SUBMITTED, // 들어와도
            null
        );

        assertThat(schedule.getSubmissionStatus()).isNull(); // 버려짐
    }

    @Test
    void 면접일정을_생성한다_INTERVIEW() {
        // when
        Schedule schedule = Schedule.interviewCreate(
            application,
            StageType.INTERVIEW,
            InterviewType.TECH,
            null,
            LocalDateTime.of(2025, 12, 10, 19, 0),
            "  서울  ",
            "  https://zoom.us/...  ",
            StageStatus.WAITING,
            ApplicationStatus.IN_PROGRESS
        );

        // then
        assertThat(schedule).extracting(
            Schedule::getAuthor,
            Schedule::getApplication,
            Schedule::getStageType,
            Schedule::getInterviewType,
            Schedule::getInterviewTypeDetail,
            Schedule::getDatetime,
            Schedule::getLocation,
            Schedule::getLink,
            Schedule::getStageStatus,
            Schedule::getSubmissionStatus,
            Schedule::getApplicationStatus
        ).containsExactly(
            author,
            application,
            StageType.INTERVIEW,
            InterviewType.TECH,
            null,
            LocalDateTime.of(2025, 12, 10, 19, 0),
            "서울",
            "https://zoom.us/...",
            StageStatus.WAITING,
            null, // interviewCreate에서는 submissionStatus 사용 안 함
            ApplicationStatus.IN_PROGRESS
        );
    }

    @Test
    void interviewCreate는_datetime이_null이면_NPE() {
        assertThatThrownBy(() -> Schedule.interviewCreate(
            application,
            StageType.INTERVIEW,
            InterviewType.TECH,
            null,
            null, // requireNonNull(datetime)
            "서울",
            "https://zoom.us/...",
            StageStatus.WAITING,
            ApplicationStatus.IN_PROGRESS
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void normalize는_blank면_null로_바뀐다() {
        // when
        Schedule schedule = Schedule.etcCreate(
            application,
            StageType.ETC,
            "   ",         // blank -> null
            LocalDateTime.of(2025, 12, 5, 23, 59),
            "   ",         // blank -> null
            "   ",         // blank -> null
            StageStatus.WAITING,
            null,
            null
        );

        // then
        assertThat(schedule.getStageName()).isNull();
        assertThat(schedule.getLocation()).isNull();
        assertThat(schedule.getLink()).isNull();
    }
}
