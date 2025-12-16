package org.squad.careerhub.infrastructure.jobposting.ai.prompt;

import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;

@Component
public class DefaultJobPostingPromptFactory implements JobPostingPromptFactory {

    @Override
    public String createSystemPrompt() {
        return """
너는 채용공고 원문 텍스트(content)에서 사실만 추출하는 정보추출기다.

[절대 규칙]
1) 출력은 "오직" JSON 1개 객체만 허용한다.
   - 설명/문장/마크다운/코드블록(```)/주석(//, /* */)/스키마 재출력/예시/추론 과정/추가 텍스트: 전부 금지
   - 응답의 첫 글자는 반드시 '{' 이고 마지막 글자는 반드시 '}' 이어야 한다.
2) content에 있는 정보만 사용한다. 지어내기/추측/외부지식 사용 금지.
3) 한국어를 다른 언어로 번역 금지. (원문이 한국어가 아니더라도 번역하지 말고, 원문 그대로 추출)
4) 아래 필드 6개만 포함한다. 추가 필드 금지. 키 이름 변경 금지.
5) JSON은 반드시 파싱 가능한 유효한 형식이어야 한다.
   - 큰따옴표(") 사용, 후행 콤마 금지, 배열/문자열 형식 준수

[값 규칙]
- company: 못 찾으면 "-"
- position: 못 찾으면 "-"
- workplace: 못 찾으면 "-"
- deadline:
  - 마감일을 못 찾거나 없거나 상시채용이면 null
  - 찾았으면 "yyyy-MM-dd HH:mm:ss" 형식(시간을 모르면 23:59:59로 보정해도 됨)
- recruitmentProcess: 못 찾으면 [] (빈 배열)
- status: "SUCCESS" | "PARTIAL" | "FAILED" 중 하나
  - FAILED: URL이 채용공고가 아니거나, 채용공고 텍스트 추출이 매우 불완전하여 핵심 정보를 거의 추출할 수 없는 경우
  - SUCCESS: 대부분 필드가 채워짐(회사/직무/근무지/전형 중 다수 확보)
  - PARTIAL: 일부만 채워짐

[최종 점검]
응답을 내기 직전에, 네가 만든 출력이 위 규칙을 모두 만족하는지 확인하라.
        """;
    }

    @Override
    public String createUserPrompt(JobPostingContentReadResult result) {
        JobPostingContent c = result.content();

        return """
[채용공고 원문 텍스트]
URL: %s
DOMAIN: %s
TITLE: %s
BODY:
%s

[요청]
아래 JSON 스키마를 "그대로" 따르되, 스키마 자체를 출력하지 말고 "결과 JSON"만 출력하라.
반드시 아래 6개 키만 포함하고, 키 순서도 그대로 유지하라.
추가 텍스트/설명/코드블록/주석은 절대 출력하지 마라.

{
  "company": string,
  "position": string,
  "deadline": string | null,
  "workplace": string,
  "recruitmentProcess": string[],
  "status": "SUCCESS" | "PARTIAL" | "FAILED"
}
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
