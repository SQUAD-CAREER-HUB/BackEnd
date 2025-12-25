package org.squad.careerhub.domain.application.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;

public interface ApplicationStageJpaRepository extends JpaRepository<ApplicationStage, Long> {

    List<ApplicationStage> findByApplicationId(Long applicationId);
    Optional<ApplicationStage> findByApplicationIdAndStageType(Long applicationId, StageType stageType);
}