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
import org.squad.careerhub.domain.application.entity.StageStatus;
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
            LocalDate.of(2024, 12, 31),
            LocalDate.of(2024, 10, 1)
        );

        etcStage = createStage(application, StageType.ETC);
        interviewStage = createStage(application, StageType.INTERVIEW);
        documentStage = createStage(application, StageType.DOCUMENT);
    }

    private ApplicationStage createStage(Application application, StageType stageType) {
        String stageName = stageType.getDescription();
        SubmissionStatus submissionStatus =
            (stageType == StageType.DOCUMENT) ? SubmissionStatus.NOT_SUBMITTED : null;

        return ApplicationStage.create(application, stageType, stageName, submissionStatus);
    }

    @Test
    void 기타일정을_생성한다_ETC() {
        // when
        Schedule schedule = Schedule.createEtc(
            etcStage,
            "  코딩테스트  ",
            LocalDateTime.of(2025, 12, 5, 23, 59),
            LocalDateTime.of(2025, 12, 6, 1, 0)
        );

        // then
        assertThat(schedule.getStage()).isEqualTo(etcStage);
        assertThat(schedule.getAuthor()).isEqualTo(author);

        assertThat(schedule.getScheduleName()).isEqualTo("코딩테스트"); // trim
        assertThat(schedule.getStartedAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
        assertThat(schedule.getEndedAt()).isEqualTo(LocalDateTime.of(2025, 12, 6, 1, 0));

        // ETC 규칙
        assertThat(schedule.getLocation()).isNull();
        assertThat(schedule.getScheduleResult()).isEqualTo(StageStatus.WAITING);
    }

    @Test
    void createEtc는_전형이_ETC가_아니면_INVALID_SCHEDULE_TYPE_RULE() {
        // given
        LocalDateTime t = LocalDateTime.of(2025, 12, 5, 23, 59);

        // when & then
        assertThatThrownBy(() -> Schedule.createEtc(
            interviewStage, // INTERVIEW stage
            "코딩테스트",
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
        Schedule schedule = Schedule.createInterview(
            interviewStage,
            "  1차 면접  ",
            LocalDateTime.of(2025, 12, 10, 19, 0),
            "  서울  "
        );

        // then
        assertThat(schedule.getStage()).isEqualTo(interviewStage);
        assertThat(schedule.getAuthor()).isEqualTo(author);

        assertThat(schedule.getScheduleName()).isEqualTo("1차 면접");
        assertThat(schedule.getStartedAt()).isEqualTo(LocalDateTime.of(2025, 12, 10, 19, 0));
        assertThat(schedule.getLocation()).isEqualTo("서울");

        // INTERVIEW 규칙
        assertThat(schedule.getEndedAt()).isNull();
        assertThat(schedule.getScheduleResult()).isEqualTo(StageStatus.WAITING);
    }

    @Test
    void createInterview는_전형이_INTERVIEW가_아니면_INVALID_SCHEDULE_TYPE_RULE() {
        // given
        LocalDateTime t = LocalDateTime.of(2025, 12, 10, 19, 0);

        // when & then
        assertThatThrownBy(() -> Schedule.createInterview(
            etcStage, // ETC stage
            "1차 면접",
            t,
            "서울"
        ))
            .isInstanceOf(CareerHubException.class)
            .extracting("errorStatus")
            .isEqualTo(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
    }

    @Test
    void createInterview는_startedAt이_null이면_NPE() {
        assertThatThrownBy(() -> Schedule.createInterview(
            interviewStage,
            "1차 면접",
            null,
            "서울"
        )).isInstanceOf(NullPointerException.class);
    }
}