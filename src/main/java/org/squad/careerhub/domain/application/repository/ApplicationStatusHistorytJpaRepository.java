package org.squad.careerhub.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.ApplicationStatusHistory;

public interface ApplicationStatusHistorytJpaRepository extends JpaRepository<ApplicationStatusHistory, Long> {

}