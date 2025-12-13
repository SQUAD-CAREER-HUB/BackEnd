package org.squad.careerhub.domain.jobposting.service.port;

import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;

public interface JobPostingContentReaderPort {
    JobPostingContentReadResult read(String url);
}

