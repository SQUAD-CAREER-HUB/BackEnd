package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.application.service.dto.NewStage;
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
            ApplicationStatus.IN_PROGRESS,
            new NewEtcSchedule(StageType.ETC, "코딩 테스트", LocalDateTime.now(), null),
            List.of()
        );

        var documentStage = new NewStage(
            StageType.DOCUMENT,
            SubmissionStatus.SUBMITTED,
            ApplicationStatus.IN_PROGRESS,
            new NewEtcSchedule(
                StageType.ETC,
                "코딩테스트",
                LocalDateTime.now().plusDays(3),
                null
            ),
            List.of()
        );

        var interviewStage = new NewStage(
            StageType.INTERVIEW,
            null,
            ApplicationStatus.IN_PROGRESS,
            new NewEtcSchedule(
                StageType.ETC,
                "코딩테스트",
                LocalDateTime.now().plusDays(3),
                null
            ),
            List.of(new NewInterviewSchedule(
                StageType.INTERVIEW,
                "임원 면접",
                LocalDateTime.now(),
                "서울 본사"
            ))
        );

        // when & then
        applicationPolicyValidator.validateNewStage(etcNewStage);

        // 서류 전형과 면접 전형에 기타 일정이 포함된 경우 예외 발생
        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(documentStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());

        assertThatThrownBy(() -> applicationPolicyValidator.validateNewStage(interviewStage))
            .isInstanceOf(CareerHubException.class)
            .hasMessage(ErrorStatus.INVALID_ETC_STAGE_RULE.getMessage());
    }

    @Test
    void 면접_일정은_면접_전형일_때만_가능하다() {
        // given
        var etcNewStage = new NewStage(
            StageType.ETC,
            null,
            null,
            null,
            List.of(new NewInterviewSchedule(
                StageType.INTERVIEW,
                "임원 면접",
                LocalDateTime.now(),
                "서울 본사"
                )
            )
        );

        var documentStage = new NewStage(
            StageType.DOCUMENT,
            SubmissionStatus.SUBMITTED,
            null,
            null,
            List.of(new NewInterviewSchedule(
                StageType.INTERVIEW,
                "임원 면접",
                LocalDateTime.now(),
                "서울 본사"
                )
            )
        );

        var interviewStage = new NewStage(
            StageType.INTERVIEW,
            null,
            null,
            null,
            List.of(new NewInterviewSchedule(
                StageType.INTERVIEW,
                "임원 면접",
                LocalDateTime.now(),
                "서울 본사"
                )
            )
        );

        var finalStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
                .newInterviewSchedules(
                    List.of(new NewInterviewSchedule(
                        StageType.INTERVIEW,
                        "임원 면접",
                        LocalDateTime.now(),
                        "서울 본사")
                    )
                )
                .build();
        // when & then
        applicationPolicyValidator.validateNewStage(interviewStage);

        // 기타 전형과 서류 전형에 면접 일정이 포함된 경우 예외 발생
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

}