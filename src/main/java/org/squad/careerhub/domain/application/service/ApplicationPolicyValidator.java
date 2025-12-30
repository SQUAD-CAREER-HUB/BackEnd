package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class ApplicationPolicyValidator {

    // 지원서 생성 시 전형에 따른 입력 규칙 검증
    public void validateNewStage(NewStage newStage, ApplicationStatus finalApplicationStatus) {
        // 서류 전형 일정은 서류 전형 단계 일 때만 입력 가능
        if (newStage.newDocsSchedule() != null && !newStage.stageType().isDocument()) {
            throw new CareerHubException(ErrorStatus.INVALID_DOCS_STAGE_RULE);
        }

        // 기타 전형 일정은 기타 전형 단계 일 때만 입력 가능
        if (!newStage.newEtcSchedules().isEmpty() && !newStage.stageType().isEtc()) {
            throw new CareerHubException(ErrorStatus.INVALID_ETC_STAGE_RULE);
        }

        // 면접 일정은 면접 전형 단계일 때만 입력 가능
        if (!newStage.newInterviewSchedules().isEmpty() && !newStage.stageType().isInterview()) {
            throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
        }

        // 지원서 최종 상태는 지원 종료 단계에서만 입력 가능
        if (finalApplicationStatus != ApplicationStatus.IN_PROGRESS && !newStage.stageType().isApplicationClose()) {
            throw new CareerHubException(ErrorStatus.INVALID_FINAL_APPLICATION_STATUS_RULE);
        }
    }

}