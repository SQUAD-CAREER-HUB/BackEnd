package org.squad.careerhub.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.global.entity.EntityStatus;

public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {

    int countByAuthorIdAndStatus(Long authorId, EntityStatus status);
    int countByAuthorIdAndCurrentStageTypeAndStatus(Long authorId, StageType stageType, EntityStatus status);
    int countByAuthorIdAndApplicationStatusAndStatus(Long authorId, ApplicationStatus applicationStatus, EntityStatus status);

}