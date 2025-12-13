package org.squad.careerhub.infrastructure.jobposting.ai.prompt;

import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;

public interface JobPostingPromptFactory {

    String createSystemPrompt();

    String createUserPrompt(JobPostingContentReadResult content);
}