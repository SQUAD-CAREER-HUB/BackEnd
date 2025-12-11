package org.squad.careerhub.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobPostingService {
    private final JobPostingExtractionPort jobPostingExtractionPort;
    private final JobPostingValidator jobPostingValidator;
    private final JobPostingManager jobPostingManager;

    public JobPostingExtractResponse extractJobPosting(String url) {
        jobPostingValidator.validateJobPostingUrl(url);
        JobPostingContentReadResult result = jobPostingManager.getJobPostingContent(url);
        jobPostingValidator.validateReadResult(result);

        return jobPostingManager.extractJobPosting(result);
    }
}
