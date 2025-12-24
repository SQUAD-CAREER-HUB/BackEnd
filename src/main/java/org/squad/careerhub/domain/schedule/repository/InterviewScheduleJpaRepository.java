package org.squad.careerhub.domain.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.schedule.entity.Schedule;

public interface InterviewScheduleJpaRepository extends JpaRepository<Schedule, Long> {

}