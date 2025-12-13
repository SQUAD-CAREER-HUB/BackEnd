package org.squad.careerhub.infrastructure.jobposting.ai.prompt;

import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;

@Component
public class DefaultJobPostingPromptFactory implements JobPostingPromptFactory {

    @Override
    public String createSystemPrompt() {
        return """
너는 주어진 content의 채용공고를 분석해서 구조화된 정보를 추출하는 어시스턴트야.
            
     ⚠ 중요 규칙:
     - content를 통한 웹 페이지에서 가져온 정보만 추출해야해. 절대 지어내거나 만들어내면 안된다.
     - 무조건 주어진 정보로만 정보를 추출해야해 한국어가 아닌 다른 언어로 번역하는건 절대 금지야.
     - 출력은 반드시 JSON 형식이어야 하고, 내가 지정한 필드만 포함해야 한다.
     - 회사명, 직무명, 마감일, 근무지, 주요업무, 자격요건, 우대사항, 채용 전형을 한국어 그대로 추출한다.
     - 마감일 정보를 못 찾거나 없거나 상시채용이면 deadline은 null로 둔다.
     - 마감일 정보를 찾은 경우 "yyyy-MM-dd HH:mm:ss"로 표현한다.
     - 회사명, 직무명, 근무지 정보를 못 찾거나 없으면 "-"로 둔다.
     - 배열로 나타내는 필드는 값을 못 찾거나 모르는 경우 빈 배열([])로 응답한다.
     - status 필드는 반드시 "SUCCESS" | "PARTIAL" | "FAILED" 중 하나다.
     - 모든 값을 성공적으로 가져왔으면 "SUCCESS",
         부분적으로 가져왔으면 "PARTIAL",
         URL을 통해서 채용공고 정보를 추출하기 어렵거나 모르거나 URL이 올바르지 않거나 채용공고와 상관없는 내용을 가지고 있는 URL이라면 "FAILED"를 반환한다.
        """;
    }

    @Override
    public String createUserPrompt(JobPostingContentReadResult result) {
        JobPostingContent c = result.content();

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
            
       주석은 절대 달지마. 
       주석은 무조건 제거해.
       한국어가 아닌 다른 언어로 번역하는건 절대 안된다. 
       주어진 정보로만 답변해야지 이걸 영어로 번역하거나 한국어가 아닌 다른 언어로 번역하는 건 금지야.
       위 텍스트에서 다음 정보를 아래 주어진 JSON 형태로만 답변해:

          {
            "company": string or '-',
            "position": string or '-',
            "deadline": string or null,
            "workplace": string,
            "recruitmentProcess": [string, ...] or [],
            "mainTasks": [string, ...] or [],
            "requiredQualifications": [string, ...] or [],
            "preferredQualifications": [string, ...] or [],
            "status": "SUCCESS" | "PARTIAL" | "FAILED"
          }
          
        """.formatted(
            c.url(),
            c.domain(),
            c.title(),
            c.bodyText()
        );
    }
}
