package org.squad.careerhub.domain.application.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.global.entity.EntityStatus;

public interface ApplicationAttachmentJpaRepository extends JpaRepository<ApplicationAttachment, Long> {

    List<ApplicationAttachment> findAllByApplicationIdAndStatus(Long applicationId, EntityStatus status);
}