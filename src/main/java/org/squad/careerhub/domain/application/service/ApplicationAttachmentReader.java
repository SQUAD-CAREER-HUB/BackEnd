package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Component
public class ApplicationAttachmentReader {

    private final ApplicationAttachmentJpaRepository applicationAttachmentJpaRepository;

    public List<ApplicationAttachment> findAttachments(Long applicationId) {
        return applicationAttachmentJpaRepository.findAllByApplicationIdAndStatus(applicationId, EntityStatus.ACTIVE);
    }

}