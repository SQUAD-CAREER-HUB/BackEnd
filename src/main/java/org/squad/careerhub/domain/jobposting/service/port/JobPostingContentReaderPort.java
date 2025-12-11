package org.squad.careerhub.domain.jobposting.service.port;

import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;

@Component
public interface JobPostingContentReaderPort {
    JobPostingContentReadResult read(String url);
}

