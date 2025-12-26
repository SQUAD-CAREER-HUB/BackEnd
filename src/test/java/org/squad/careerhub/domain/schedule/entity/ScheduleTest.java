package org.squad.careerhub.domain.schedule.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ScheduleTest {

    Member author;
    Application application;

    ApplicationStage etcStage;
    ApplicationStage interviewStage;
    ApplicationStage documentStage;

    @BeforeEach
    void setUp() {
        author = Member.create(
            "email@gmail.com",
            SocialProvider.KAKAO,
            "socialId",
            "nickname",
            "profileImageUrl"
        );

        application = Application.create(
            author,
            "http://jobposting.url",
            "CompanyName",
            "PositionName",
            "JobLocation",
            StageType.DOCUMENT,
            ApplicationStatus.IN_PROGRESS,
            ApplicationMethod.EMAIL,
            LocalDateTime.of(2024, 12, 31, 23, 59, 59)
        );

        etcStage = createStage(application, StageType.ETC);
        interviewStage = createStage(application, StageType.INTERVIEW);
        documentStage = createStage(application, StageType.DOCUMENT);
    }

    private ApplicationStage createStage(Application application, StageType stageType) {
        return ApplicationStage.create(application, stageType);
    }

    @Test
    void 기타일정을_생성한다_ETC() {
        // when
        Schedule schedule = Schedule.register(
            author,
            etcStage,
            "코딩테스트",
            null,
            ScheduleResult.WAITING,
            null,
            LocalDateTime.of(2025, 12, 5, 23, 59),
            LocalDateTime.of(2025, 12, 6, 1, 0)
        );

        // then
        assertThat(schedule.getApplicationStage()).isEqualTo(etcStage);
        assertThat(schedule.getAuthor()).isEqualTo(author);

        assertThat(schedule.getScheduleName()).isEqualTo("코딩테스트"); // trim
        assertThat(schedule.getStartedAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
        assertThat(schedule.getEndedAt()).isEqualTo(LocalDateTime.of(2025, 12, 6, 1, 0));

        // ETC 규칙
        assertThat(schedule.getLocation()).isNull();
        assertThat(schedule.getScheduleResult()).isEqualTo(ScheduleResult.WAITING);
    }

    @Test
    void createEtc는_전형이_ETC가_아니면_INVALID_SCHEDULE_TYPE_RULE() {
        // given
        LocalDateTime t = LocalDateTime.of(2025, 12, 5, 23, 59);

        // when & then
        assertThatThrownBy(() -> Schedule.register(
            author,
            interviewStage, // INTERVIEW stage
            "코딩테스트",
            null,
            ScheduleResult.WAITING,
            null,
            t,
            t.plusHours(1)
        ))
            .isInstanceOf(CareerHubException.class)
            .extracting("errorStatus")
            .isEqualTo(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
    }

    @Test
    void 면접일정을_생성한다_INTERVIEW() {
        // when
        Schedule schedule = Schedule.register(
            author,
            interviewStage,
            "1차 면접",
            "강남구 테헤란로",
            ScheduleResult.WAITING,
            null,
            LocalDateTime.of(2025, 12, 10, 19, 0),
            null
        );

        // then
        assertThat(schedule.getApplicationStage()).isEqualTo(interviewStage);
        assertThat(schedule.getAuthor()).isEqualTo(author);

        assertThat(schedule.getScheduleName()).isEqualTo("1차 면접");
        assertThat(schedule.getStartedAt()).isEqualTo(LocalDateTime.of(2025, 12, 10, 19, 0));
        assertThat(schedule.getLocation()).isEqualTo("서울");

        // INTERVIEW 규칙
        assertThat(schedule.getEndedAt()).isNull();
        assertThat(schedule.getScheduleResult()).isEqualTo(ScheduleResult.WAITING);
    }

    @Test
    void createInterview는_전형이_INTERVIEW가_아니면_INVALID_SCHEDULE_TYPE_RULE() {
        // given
        LocalDateTime t = LocalDateTime.of(2025, 12, 10, 19, 0);

        // when & then
        assertThatThrownBy(() -> Schedule.register(
            author,
            etcStage, // ETC stage
            "1차 면접",
            "강남구 테헤란로",
            ScheduleResult.WAITING,
            null,
            t,
            null
        ))
            .isInstanceOf(CareerHubException.class)
            .extracting("errorStatus")
            .isEqualTo(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
    }

    @Test
    void createInterview는_startedAt이_null이면_NPE() {
        LocalDateTime t = LocalDateTime.of(2025, 12, 10, 19, 0);
        assertThatThrownBy(() -> Schedule.register(
            author,
            etcStage, // ETC stage
            "1차 면접",
            "강남구 테헤란로",
            ScheduleResult.WAITING,
            null,
            t,
            null
        )).isInstanceOf(NullPointerException.class);
    }
}