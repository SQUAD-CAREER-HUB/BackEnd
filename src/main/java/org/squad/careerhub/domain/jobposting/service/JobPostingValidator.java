package org.squad.careerhub.domain.jobposting.service;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class JobPostingValidator {

    public void validateJobPostingUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }

        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new CareerHubException(ErrorStatus.BAD_REQUEST);
            }
            String host = uri.getHost();
            if (!SUPPORTED_HOSTS.contains(host)) {
                throw new CareerHubException(ErrorStatus.BAD_REQUEST);
            }
        } catch (URISyntaxException e) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }
    }

    public void validateReadResult(JobPostingContentReadResult result) {
        if (result.status() == JobPostingContentReadStatus.SUCCESS) {
            return;
        }

        ErrorStatus errorStatus = switch (result.status()) {
            case DISALLOWED_BY_ROBOTS -> ErrorStatus.JOB_POSTING_ROBOTS_BLOCKED;
            case NEED_LOGIN -> ErrorStatus.JOB_POSTING_NEED_LOGIN;
            case JS_RENDERING_NOT_SUPPORTED -> ErrorStatus.JOB_POSTING_JS_RENDER_REQUIRED;
            case UNKNOWN_ERROR -> ErrorStatus.JOB_POSTING_READ_FAILED;
            case SUCCESS -> throw new IllegalStateException(
                "SUCCESS 상태는 validateReadResult 진입 전 return 되어야 합니다."
            );
        };

        throw new CareerHubException(errorStatus);
    }


    private static final Set<String> SUPPORTED_HOSTS = Set.of(
        "www.wanted.co.kr",
        "wanted.co.kr",
        "www.saramin.co.kr",
        "www.jobkorea.co.kr",
        "www.rallit.com"
    );

}
