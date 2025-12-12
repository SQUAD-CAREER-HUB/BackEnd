package org.squad.careerhub.infrastructure.jobposting.ai.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.infrastructure.jobposting.dto.JobPostingAiResult;
import org.squad.careerhub.infrastructure.jobposting.ai.prompt.JobPostingPromptFactory;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAiJobPostingExtractionAdapter implements JobPostingExtractionPort {

    protected final ChatClient chatClient;
    protected final JobPostingPromptFactory promptFactory;
    protected abstract String providerName();

    @Override
    public JobPostingExtractResponse extractFromContent(JobPostingContentReadResult content) {
        String systemPrompt = promptFactory.createSystemPrompt();
        String userPrompt = promptFactory.createUserPrompt(content);
        long t1 = System.currentTimeMillis();
        JobPostingAiResult aiResult = chatClient
            .prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .call()
            .entity(JobPostingAiResult.class);
        long t2 = System.currentTimeMillis();
        log.debug("[{}] job posting extracted. url={}, status={}",
            providerName(), content.content().url(), aiResult.status());

        JobPostingExtractStatus status = mapStatus(aiResult.status());
        log.info("[Timing] total={}ms",
            (t2 - t1));
        return JobPostingExtractResponse.builder()
            .company(aiResult.company())
            .position(aiResult.position())
            .deadline(aiResult.deadline())
            .workplace(aiResult.workplace())
            .recruitmentProcess(aiResult.recruitmentProcess())
            .mainTasks(aiResult.mainTasks())
            .requiredQualifications(aiResult.requiredQualifications())
            .preferredQualifications(aiResult.preferredQualifications())
            .status(status)
            .build();
    }

    protected JobPostingExtractStatus mapStatus(String status) {
        if (status == null) {
            return JobPostingExtractStatus.FAILED;
        }
        try {
            return JobPostingExtractStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return JobPostingExtractStatus.FAILED;
        }
    }
}
