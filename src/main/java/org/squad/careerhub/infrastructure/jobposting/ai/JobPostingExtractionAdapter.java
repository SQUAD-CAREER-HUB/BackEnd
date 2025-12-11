package org.squad.careerhub.infrastructure.jobposting.ai;

import com.google.genai.errors.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.springframework.ai.chat.client.ChatClient;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobPostingExtractionAdapter implements JobPostingExtractionPort {

    private final ChatClient chatClient;

    @Override
    public JobPostingExtractResponse extractFromContent(JobPostingContentReadResult content) {

        if (content.status() != JobPostingContentReadStatus.SUCCESS) {
            throw new CareerHubException(ErrorStatus.JOB_POSTING_READ_FAILED);
        }
        try {
            JobPostingAiResult aiResult = chatClient
                .prompt()
                .system(systemPrompt())
                .user(userPrompt(content))
                .call()
                .entity(JobPostingAiResult.class);

            return JobPostingExtractResponse.builder()
                .company(aiResult.company())
                .position(aiResult.position())
                .deadline(aiResult.deadline())
                .workplace(aiResult.workplace())
                .recruitmentProcess(aiResult.recruitmentProcess())
                .mainTasks(aiResult.mainTasks())
                .requiredQualifications(aiResult.requiredQualifications())
                .preferredQualifications(aiResult.preferredQualifications())
                .status(mapStatus(aiResult.status()))
                .build();
        } catch (RuntimeException e) {
            ErrorStatus errorStatus = resolveAiErrorStatus(e);

            log.warn("[Gemini][JobPosting] AI 호출 실패. status={}, message={}",
                errorStatus, e.getMessage(), e);

            throw new CareerHubException(errorStatus, e);
        }

    }

    private ErrorStatus resolveAiErrorStatus(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof ClientException clientEx) {
                try {
                    var statusCodeMethod = clientEx.getClass().getMethod("getStatusCode");
                    Object codeObj = statusCodeMethod.invoke(clientEx);
                    if (codeObj instanceof Number code && code.intValue() == 429) {
                        return ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED;
                    }
                } catch (Exception ignore) {
                }

                String msg = clientEx.getMessage();
                if (msg != null &&
                    (msg.contains("Quota exceeded") ||
                        msg.contains("You exceeded your current quota") ||
                        msg.contains("429"))) {
                    return ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED;
                }
            }
            cause = cause.getCause();
        }

        return ErrorStatus.JOB_POSTING_AI_FAILED;
    }

    private JobPostingExtractStatus mapStatus(String status) {
        if (status == null) {
            return JobPostingExtractStatus.FAILED;
        }
        try {
            return JobPostingExtractStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return JobPostingExtractStatus.FAILED;
        }
    }

    private String systemPrompt() {
        return """
            너는 주어진 content의 채용공고를 분석해서 구조화된 정보를 추출하는 어시스턴트야.

            ⚠ 중요 규칙:
            - content를 통한 웹 페이지에서 가져온 정보만 추출해야해. 절대 지어내거나 만들어내면 안된다.
            - 출력은 반드시 JSON 형식이어야 하고, 내가 지정한 필드만 포함해야 한다.
            - 회사명, 직무명, 마감일, 근무지, 주요업무, 자격요건, 우대사항, 채용 전형을 한국어 그대로 추출한다.
            - 마감일 정보를 못 찾거나 없거나 상시채용이면 deadline은 null로 둔다.
            - 마감일 정보를 찾은 경우 "yyyy-MM-dd"로 표현한다.
            - 근무지 정보를 못 찾거나 없으면 workplace는 "-"로 둔다.
            - 값이 없는 필드는 빈 배열([])로 응답한다.
            - status 필드는 반드시 "SUCCESS" | "PARTIAL" | "FAILED" 중 하나다.
            - 모든 값을 성공적으로 가져왔으면 "SUCCESS",
                부분적으로 가져왔으면 "PARTIAL",
                URL을 통해서 채용공고 정보를 추출하기 어렵거나 모르거나 URL이 올바르지 않거나 채용공고와 상관없는 내용을 가지고 있는 URL이라면 "FAILED"를 반환한다.
            """;
    }

    private String userPrompt(JobPostingContentReadResult content) {
        return """
            아래는 특정 채용 공고의 원문 텍스트야.
            
             [URL]
             %s
    
             [DOMAIN]
             %s
    
             [TITLE]
             %s
    
             [BODY]
             %s
             
             위 텍스트에서 다음 정보를 JSON 형태로만 답변해:
             
            {
              "company": string or '-', // 회사명
              "position": string or '-', // 직무명
              "deadline": string or '-', // 지원 마감기한
              "workplace": string, // 근무지
              "recruitmentProcess": [string, ...] or [], // 채옹절차
              "mainTasks": [string, ...] or [], // 주요 업무
              "requiredQualifications": [string, ...] or [], // 필수 자격사항
              "preferredQualifications": [string, ...] or [], // 우대사항
              "status": "SUCCESS" | "PARTIAL" | "FAILED" // 추출 성공 여부
            }
            """.formatted(
            content.content().url(),
            content.content().domain(),
            content.content().title(),
            content.content().bodyText()
        );
    }
}