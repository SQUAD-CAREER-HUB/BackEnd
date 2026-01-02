package org.squad.careerhub.domain.jobposting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingContentReaderPort;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;

@RequiredArgsConstructor
@Slf4j
@Component
public class JobPostingManager {

    private final JobPostingExtractionPort jobPostingExtractionPort;
    private final JobPostingContentReaderPort jobPostingContentReaderPort;

    public JobPostingContentReadResult getJobPostingContent(String url) {
        log.debug("[JobPostingManager] 채용 공고 콘텐츠 읽기 시작 - url: {}", url);
        JobPostingContentReadResult result = jobPostingContentReaderPort.read(url);
        log.debug("[JobPostingManager] 채용 공고 콘텐츠 읽기 완료 - url: {}", url);
        return result;
    }

    public JobPostingExtractResponse extractJobPosting(JobPostingContentReadResult content) {
        log.debug("[JobPostingManager] 채용 공고 정보 추출 시작");
        JobPostingExtractResponse response = jobPostingExtractionPort.extractFromContent(content);
        log.debug("[JobPostingManager] 채용 공고 정보 추출 완료 - company: {}, position: {}",
                response.company(), response.position());
        return response;
    }
}
