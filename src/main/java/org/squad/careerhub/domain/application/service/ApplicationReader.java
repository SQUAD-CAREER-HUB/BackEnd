package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Component
public class ApplicationReader {

    private final ApplicationJpaRepository applicationJpaRepository;

    // NOTE: 한방 쿼리로 처리해야 될 지 고민해보기
    @Transactional(readOnly = true)
    public ApplicationStatisticsResponse getApplicationStatistics(Long authorId) {
        int totalApplications = applicationJpaRepository.countByAuthorIdAndStatus(authorId, EntityStatus.ACTIVE);
        int interviewStageCount = applicationJpaRepository.countByAuthorIdAndCurrentStageTypeAndStatus(
                authorId,
                StageType.INTERVIEW,
                EntityStatus.ACTIVE
        );
        int etcStageCount = applicationJpaRepository.countByAuthorIdAndCurrentStageTypeAndStatus(
                authorId,
                StageType.ETC,
                EntityStatus.ACTIVE
        );
        int finalPassCount = applicationJpaRepository.countByAuthorIdAndCurrentStageTypeAndStatus(
                authorId,
                StageType.FINAL_PASS,
                EntityStatus.ACTIVE
        );

        return ApplicationStatisticsResponse.of(
                totalApplications,
                interviewStageCount,
                etcStageCount,
                finalPassCount
        );
    }

}