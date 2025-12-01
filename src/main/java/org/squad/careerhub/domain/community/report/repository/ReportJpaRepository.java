package org.squad.careerhub.domain.community.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.report.entity.Report;

public interface ReportJpaRepository extends JpaRepository<Report, Long> {

}