package org.squad.careerhub.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.Application;

public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {

}