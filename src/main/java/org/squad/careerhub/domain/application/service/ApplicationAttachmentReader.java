package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;

@RequiredArgsConstructor
@Component
public class ApplicationAttachmentReader {

    private final ApplicationAttachmentJpaRepository applicationAttachmentJpaRepository;

    public List<ApplicationAttachment> findAttachments(Long applicationId) {
        return applicationAttachmentJpaRepository.findAllByApplicationId(applicationId);
    }

}