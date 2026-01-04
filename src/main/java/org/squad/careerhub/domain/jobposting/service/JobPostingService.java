package org.squad.careerhub.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class JobPostingService {
    private final JobPostingValidator jobPostingValidator;
    private final JobPostingManager jobPostingManager;

    public JobPostingExtractResponse extractJobPosting(String url) {
        jobPostingValidator.validateJobPostingUrl(url);
        JobPostingContentReadResult result = jobPostingManager.getJobPostingContent(url);

        JobPostingExtractResponse response = jobPostingManager.extractJobPosting(result);

        log.info("[JobPostingService] 채용 공고 추출 완료 : {}", response.toString());

        return response;
    }
}

