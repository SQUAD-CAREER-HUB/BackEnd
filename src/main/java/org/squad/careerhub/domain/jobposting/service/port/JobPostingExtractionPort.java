package org.squad.careerhub.domain.jobposting.service.port;

import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;

@Component
public interface JobPostingExtractionPort {
    JobPostingExtractResponse extractFromContent(JobPostingContentReadResult content);
}
