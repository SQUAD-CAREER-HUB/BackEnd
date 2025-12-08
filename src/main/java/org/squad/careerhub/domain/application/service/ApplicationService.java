package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;

@RequiredArgsConstructor
@Service
public class ApplicationService {

    private final ApplicationManager applicationManager;
    private final ApplicationPolicyValidator applicationPolicyValidator;

    public void createApplication(
            NewJobPosting newJobPosting,
            NewApplicationInfo newApplicationInfo,
            Long authorId
    ) {
        applicationPolicyValidator.validateMemoRule(newApplicationInfo);

        applicationManager.create(
                newJobPosting,
                newApplicationInfo,
                authorId
        );
    }

}