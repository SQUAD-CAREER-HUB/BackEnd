package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.service.dto.NewDocsSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ApplicationPolicyValidatorUnitTest extends TestDoubleSupport {

    @InjectMocks
    private ApplicationPolicyValidator applicationPolicyValidator;

    @Test
    void 서류_일정은_서류_전형일_때만_가능하다() {
        // given
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING))
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();

        var documentStage = NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING))
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();

        var interviewStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING))
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();

        var finalStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .newEtcSchedules(List.of())
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING))
                .newInterviewSchedules(List.of())
                .build();

        // when & then
        applicationPolicyValidator.validateNewStage(documentStage, ApplicationStatus.IN_PROGRESS);

        // 기타 전형, 면접 전형, 지원 종료 에 서류 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(etcNewStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_DOCS_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(interviewStage,ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_DOCS_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(finalStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_DOCS_STAGE_RULE.getMessage());
    }


    @Test
    void 기타_일정은_기타_전형일_때만_가능하다() {
        // given
        var codingTest = new NewEtcSchedule("코딩 테스트", now(), now().plusDays(2), ScheduleResult.WAITING);
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedules(List.of(codingTest))
                .newInterviewSchedules(List.of())
                .build();

        var documentStage = NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING))
                .newEtcSchedules(List.of(codingTest))
                .newInterviewSchedules(List.of())
                .build();

        var interviewStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newEtcSchedules(List.of(codingTest))
                .newInterviewSchedules(List.of())
                .build();

        var finalStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .newEtcSchedules(List.of(codingTest))
                .newInterviewSchedules(List.of())
                .build();

        // when & then
        applicationPolicyValidator.validateNewStage(etcNewStage, ApplicationStatus.IN_PROGRESS);

        // 서류 전형, 면접 전형, 지원 종료 에 기타 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(interviewStage,ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(finalStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());
    }

    @Test
    void 면접_일정은_면접_전형일_때만_가능하다() {
        // given
        var newInterviewSchedule = new NewInterviewSchedule("1차 면접", now(), "판교", ScheduleResult.WAITING);
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newInterviewSchedules(
                        List.of(newInterviewSchedule)
                )
                .newEtcSchedules(List.of())
                .build();

        var documentStage = NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING))
                .newInterviewSchedules(
                        List.of(newInterviewSchedule)
                )
                .newEtcSchedules(List.of())
                .build();

        var interviewStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newInterviewSchedules(
                        List.of(newInterviewSchedule)
                )
                .newEtcSchedules(List.of())
                .build();

        var finalStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(
                        List.of(newInterviewSchedule)
                )
                .build();
        // when & then
        applicationPolicyValidator.validateNewStage(interviewStage, ApplicationStatus.IN_PROGRESS);

        // 기타 전형, 서류 전형, 지원 종료에 면접 일정이 포함된 경우 예외 발생
        assertThatThrownBy(
                () -> applicationPolicyValidator.validateNewStage(etcNewStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(finalStage, ApplicationStatus.IN_PROGRESS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE.getMessage());
    }

    @Test
    void 최종_지원서_상태는_지원_종료_단계일떄만_가능하다() {
        // given
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();

        var documentStage = NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();

        var interviewStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();

        var finalStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();
        // when & then
        applicationPolicyValidator.validateNewStage(finalStage, ApplicationStatus.IN_PROGRESS);

        // 서류전형, 기타 전형, 서류 전형에 면접 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage, ApplicationStatus.FINAL_PASS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE.getMessage());

        assertThatThrownBy(
                () -> applicationPolicyValidator.validateNewStage(etcNewStage, ApplicationStatus.FINAL_PASS))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(interviewStage, ApplicationStatus.FINAL_FAIL))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE.getMessage());
    }

}