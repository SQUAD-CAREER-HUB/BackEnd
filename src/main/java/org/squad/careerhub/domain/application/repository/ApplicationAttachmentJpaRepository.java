package org.squad.careerhub.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;

public interface ApplicationAttachmentJpaRepository extends JpaRepository<ApplicationAttachment, Long> {

}