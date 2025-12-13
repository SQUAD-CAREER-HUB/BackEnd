package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class ApplicationPolicyValidator {

    // 지원서 생성 시 전형에 따른 입력 규칙 검증
    public void validateNewStage(NewStage newStage) {
        // 기타 전형 일정은 기타 전형일 때만 입력 가능
        if (newStage.newEtcSchedule() != null && !newStage.stageType().equals(StageType.ETC)) {
            throw new CareerHubException(ErrorStatus.INVALID_ETC_STAGE_RULE);
        }

        // 면접 일정은 면접 전형일 때만 입력 가능
        // 컨트롤러에서 면접 일정 DTO 가 NULL 일 경우 빈 리스트로 변환해주기 때문에 NULL 체크는 불필요
        if (!newStage.newInterviewSchedules().isEmpty() && !newStage.stageType().equals(StageType.INTERVIEW)) {
            throw new CareerHubException(ErrorStatus.INVALID_SCHEDULE_TYPE_RULE);
        }
    }

}