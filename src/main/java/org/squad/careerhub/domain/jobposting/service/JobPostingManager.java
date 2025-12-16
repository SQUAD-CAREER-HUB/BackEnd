package org.squad.careerhub.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingContentReaderPort;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;

@RequiredArgsConstructor
@Component
public class JobPostingManager {

    private final JobPostingExtractionPort jobPostingExtractionPort;
    private final JobPostingContentReaderPort jobPostingContentReaderPort;

    public JobPostingContentReadResult getJobPostingContent(String url) {
        return jobPostingContentReaderPort.read(url);
    }

    public JobPostingExtractResponse extractJobPosting(JobPostingContentReadResult content) {
        return jobPostingExtractionPort.extractFromContent(content);
    }
}
