package org.squad.careerhub.domain.jobposting.service.dto;

import lombok.Builder;
import lombok.Getter;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;

@Builder
public record JobPostingContent(
    String url,
    String domain,
    String title,      // <title> 혹은 h1/h2 등에서 추출한 제목
    String bodyText // 본문 텍스트 (LLM이 읽을 내용)
) { }