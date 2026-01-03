package org.squad.careerhub.domain.schedule.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.global.entity.EntityStatus;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByApplicationStageInAndAuthorId(List<ApplicationStage> applicationStages,
            Long authorId);

    Optional<Schedule> findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
            Long id,
            Long applicationId,
            StageType stageType,
            EntityStatus status
    );
}