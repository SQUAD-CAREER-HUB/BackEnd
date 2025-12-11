package org.squad.careerhub.domain.jobposting.service.dto;

import lombok.Builder;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;

@Builder
public record JobPostingContentReadResult(
    JobPostingContentReadStatus status,
    JobPostingContent content,
    String failureReason
) {
    public static JobPostingContentReadResult success(JobPostingContent content) {
        return new JobPostingContentReadResult(JobPostingContentReadStatus.SUCCESS, content, null);
    }

    public static JobPostingContentReadResult error(JobPostingContentReadStatus status, String reason) {
        return new JobPostingContentReadResult(status, null, reason);
    }
}
