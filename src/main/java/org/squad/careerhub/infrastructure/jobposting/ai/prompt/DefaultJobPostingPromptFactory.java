package org.squad.careerhub.infrastructure.jobposting.ai.prompt;

import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;

@Component
public class DefaultJobPostingPromptFactory implements JobPostingPromptFactory {

    @Override
    public String createSystemPrompt() {
        return """
    너는 채용공고 텍스트에서 사실만 추출해 JSON 객체 1개로만 출력하는 엔진이다.
    
        [절대 규칙]
        - 출력은 오직 JSON 객체 1개다.
        - 설명/해설/분석/요약/머리말/꼬리말/마크다운/코드블록/주석/스키마라는 단어 사용: 전부 금지.
        - 출력의 첫 문자는 반드시 '{' 이고 마지막 문자는 반드시 '}' 이어야 한다.
        - 반드시 한 줄(one-line)로 출력한다. (줄바꿈 금지)
        - 키는 아래 6개만 허용. 추가/삭제/변경 금지. 순서도 유지.
    
        허용 키(순서 고정):
        company, position, deadline, workplace, recruitmentProcess, status
    
        [값 규칙]
        - company/position/workplace: 없으면 "-"
        - deadline: 없으면 null, 있으면 "yyyy-MM-dd HH:mm:ss" (시간 모르면 23:59:59)
        - recruitmentProcess: 없으면 []
        - status: SUCCESS | PARTIAL | FAILED
          - FAILED: 채용공고가 아니거나 텍스트가 너무 불완전
          - SUCCESS: 핵심정보 다수 추출
          - PARTIAL: 일부만 추출
    
        [검증]
        출력에 '{' 앞/ '}' 뒤에 문자가 하나라도 있으면 즉시 규칙 위반이다. 규칙을 만족하도록 다시 출력하라.
        """;
    }

    @Override
    public String createUserPrompt(JobPostingContentReadResult result) {
        JobPostingContent c = result.content();

        return """
        규칙: JSON 객체 1개만 출력. 다른 글자 출력 금지. 한 줄로 출력.
            
          [채용공고 원문]
          URL=%s
          DOMAIN=%s
          TITLE=%s
          BODY=%s
    
          아래 JSON을 그대로 복사한 다음 값만 채워서 출력해라. (키/순서/형식 변경 금지)
          Explanation 이나 어떤 아무 말이나 모든게 출력 금지야.
          JSON 이외 다른 출력은 절대 안돼
          출력:
          {"company":"-","position":"-","deadline":null,"workplace":"-","recruitmentProcess":[],"status":"PARTIAL"}
        """.formatted(
            safe(c.url()),
            safe(c.domain()),
            safe(c.title()),
            safe(c.bodyText())
        );
    }

    // 프롬프트 깨짐 방지(Null-safe)
    private String safe(String s) {
        return s == null ? "" : s;
    }
}
