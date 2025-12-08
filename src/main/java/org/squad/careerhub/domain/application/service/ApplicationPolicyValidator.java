package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class ApplicationPolicyValidator {

    // 지원서 생성 시 기타 전형 진행 중일 때만 메모 작성 가능
    public void validateMemoRule(NewApplicationInfo newApplicationInfo) {
        if (
                !newApplicationInfo.memo().isEmpty() &&
                !newApplicationInfo.applicationStatus().equals(ApplicationStatus.WAITING_FOR_ETC)
        ) {
            throw new CareerHubException(ErrorStatus.INVALID_MEMO_RULE);
        }
    }

}