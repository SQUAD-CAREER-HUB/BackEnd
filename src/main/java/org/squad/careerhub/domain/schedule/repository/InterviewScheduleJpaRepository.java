package org.squad.careerhub.domain.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.schedule.entity.InterviewSchedule;

public interface InterviewScheduleJpaRepository extends JpaRepository<InterviewSchedule, Long> {

}