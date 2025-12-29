package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ApplicationPolicyValidatorTest extends TestDoubleSupport {

    @InjectMocks
    private ApplicationPolicyValidator applicationPolicyValidator;

    @Test
    void 기타_일정은_기타_전형일_때만_가능하다() {
        // given
        var etcNewStage = new NewStage(
            StageType.ETC,
            null,
            null,
            Collections.singletonList(
                new NewEtcSchedule("코딩 테스트", now(), null)),
            List.of()
        );

        var documentStage = new NewStage(
            StageType.DOCUMENT,
            SubmissionStatus.SUBMITTED,
            null,
            Collections.singletonList(new NewEtcSchedule(
                "코딩테스트",
                now().plusDays(3),
                null
            )),
            List.of()
        );

        var interviewStage = NewStage.builder()
            .stageType(StageType.INTERVIEW)
            .newEtcSchedules(
                List.of(new NewEtcSchedule("코딩 테스트", now(), now().plusDays(2))))
            .newInterviewSchedules(List.of())
            .build();

        var finalStage = NewStage.builder()
            .stageType(StageType.APPLICATION_CLOSE)
            .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
            .newEtcSchedules(
                List.of(new NewEtcSchedule("코딩 테스트", now(), now().plusDays(2))))
            .newInterviewSchedules(List.of())
            .build();

        // when & then
        applicationPolicyValidator.validateNewStage(etcNewStage);

        // 서류 전형, 면접 전형, 지원 종료 에 기타 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(interviewStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(finalStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());
    }

    @Test
    void 면접_일정은_면접_전형일_때만_가능하다() {
        // given
        var etcNewStage = NewStage.builder()
            .stageType(StageType.ETC)
            .newInterviewSchedules(
                List.of(new NewInterviewSchedule("1차 면접", now(), "서울 본사"))
            )
            .newEtcSchedules(List.of())
            .build();

        var documentStage = NewStage.builder()
            .stageType(StageType.DOCUMENT)
            .submissionStatus(SubmissionStatus.SUBMITTED)
            .newInterviewSchedules(
                List.of(new NewInterviewSchedule("1차 면접", now(), "서울 본사"))
            )
            .newEtcSchedules(List.of())
            .build();

        var interviewStage = NewStage.builder()
            .stageType(StageType.INTERVIEW)
            .newInterviewSchedules(
                List.of(new NewInterviewSchedule("1차 면접", now(), "서울 본사"))
            )
            .newEtcSchedules(List.of())
            .build();

        var finalStage = NewStage.builder()
            .stageType(StageType.APPLICATION_CLOSE)
            .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
            .newEtcSchedules(List.of())
            .newInterviewSchedules(
                List.of(new NewInterviewSchedule("1차 면접", now(), "서울 본사"))
            )
            .build();
        // when & then
        applicationPolicyValidator.validateNewStage(interviewStage);

        // 기타 전형, 서류 전형, 지원 종료에 면접 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(etcNewStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(finalStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE.getMessage());
    }

    @Test
    void 최종_지원서_상태는_지원_종료_단계일떄만_가능하다() {
        // given
        var etcNewStage = NewStage.builder()
            .stageType(StageType.ETC)
            .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
            .newEtcSchedules(List.of())
            .newInterviewSchedules(List.of())
            .build();

        var documentStage = NewStage.builder()
            .stageType(StageType.DOCUMENT)
            .submissionStatus(SubmissionStatus.SUBMITTED)
            .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
            .newEtcSchedules(List.of())
            .newInterviewSchedules(List.of())
            .build();

        var interviewStage = NewStage.builder()
            .stageType(StageType.INTERVIEW)
            .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
            .newEtcSchedules(List.of())
            .newInterviewSchedules(List.of())
            .build();

        var finalStage = NewStage.builder()
            .stageType(StageType.APPLICATION_CLOSE)
            .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
            .newEtcSchedules(List.of())
            .newInterviewSchedules(List.of())
            .build();
        // when & then
        applicationPolicyValidator.validateNewStage(finalStage);

        // 서류전형, 기타 전형, 서류 전형에 면접 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(etcNewStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(interviewStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE.getMessage());
    }

}