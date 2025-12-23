package org.squad.careerhub.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;

@RequiredArgsConstructor
@Service
public class JobPostingService {
    private final JobPostingValidator jobPostingValidator;
    private final JobPostingManager jobPostingManager;

    public JobPostingExtractResponse extractJobPosting(String url) {
        jobPostingValidator.validateJobPostingUrl(url);
        JobPostingContentReadResult result = jobPostingManager.getJobPostingContent(url);
        jobPostingValidator.validateReadResult(result);

        return jobPostingManager.extractJobPosting(result);
    }
}
