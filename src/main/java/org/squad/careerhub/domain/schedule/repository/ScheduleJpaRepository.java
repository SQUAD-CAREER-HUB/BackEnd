package org.squad.careerhub.domain.schedule.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.schedule.entity.Schedule;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByApplicationStageInAndAuthorId(List<ApplicationStage> applicationStages, Long authorId);
}