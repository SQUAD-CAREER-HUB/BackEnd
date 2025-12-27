package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;

@RequiredArgsConstructor
@Component
public class ApplicationStageReader {

    private final ApplicationStageJpaRepository applicationStageJpaRepository;

    public List<ApplicationStage> findApplicationStages(Long applicationId) {
        return applicationStageJpaRepository.findByApplicationId(applicationId);
    }

}