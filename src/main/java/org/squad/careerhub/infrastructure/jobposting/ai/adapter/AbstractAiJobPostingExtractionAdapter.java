package org.squad.careerhub.infrastructure.jobposting.ai.adapter;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
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
        if (content == null || content.content() == null) {
            return JobPostingExtractResponse.builder()
                .status(JobPostingExtractStatus.FAILED)
                .build();
        }
        String systemPrompt = promptFactory.createSystemPrompt();
        String userPrompt = promptFactory.createUserPrompt(content);
        long t1 = System.currentTimeMillis();

        JobPostingAiResult aiResult;
        try {
            aiResult = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(JobPostingAiResult.class);
        } catch (Throwable e) {
            long t2 = System.currentTimeMillis();

            ErrorStatus status = resolveAiErrorStatus(e);
            log.warn("[{}] AI call failed total={}ms, mappedError={}",
                providerName(), (t2 - t1), status, e);

            throw new CareerHubException(status);
        }

        log.debug("[{}] job posting extracted. url={}, status={}",
            providerName(), content.content().url(), aiResult.status());

        JobPostingExtractStatus status = mapStatus(aiResult.status());

        return JobPostingExtractResponse.builder()
            .url(content.content().url())
            .company(aiResult.company())
            .position(aiResult.position())
            .deadline(aiResult.deadline())
            .workplace(aiResult.workplace())
            .recruitmentProcess(aiResult.recruitmentProcess())
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

    private ErrorStatus resolveAiErrorStatus(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            // 1) statusCode 값을 가진 예외(WebClient/HttpClientErrorException 등)를 반사로 폭넓게 처리
            try {
                var statusCodeMethod = cause.getClass().getMethod("getStatusCode");
                Object codeObj = statusCodeMethod.invoke(cause);

                // getStatusCode()가 HttpStatusCode/HttpStatus/Number 등 여러 타입일 수 있어서 유연하게 처리
                int code = -1;
                if (codeObj instanceof Number n) {
                    code = n.intValue();
                } else {
                    try {
                        var valueMethod = codeObj.getClass().getMethod("value");
                        Object v = valueMethod.invoke(codeObj);
                        if (v instanceof Number n2) code = n2.intValue();
                    } catch (Exception ignore) {
                    }
                }

                if (code == 429) {
                    return ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED;
                }
            } catch (Exception ignore) {
                // ignore
            }

            String msg = cause.getMessage();
            if (msg != null) {
                String m = msg.toLowerCase(Locale.ROOT);
                if (m.contains("quota exceeded")
                    || m.contains("exceeded your current quota")
                    || m.contains("rate limit")
                    || m.contains("too many requests")
                    || m.contains("429")) {
                    return ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED;
                }
            }

            cause = cause.getCause();
        }

        return ErrorStatus.JOB_POSTING_AI_FAILED;
    }
}
