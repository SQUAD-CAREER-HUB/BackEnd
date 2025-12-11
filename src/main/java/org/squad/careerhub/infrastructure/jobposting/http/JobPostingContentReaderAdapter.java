package org.squad.careerhub.infrastructure.jobposting.http;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingContentReaderPort;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobPostingContentReaderAdapter implements JobPostingContentReaderPort {

    private final WebClient webClient;
    private final RobotsTxtChecker robotsTxtChecker;
    private final SaraminUrlNormalizer saraminUrlNormalizer;

    @Override
    public JobPostingContentReadResult read(String url) {

        String normalizedUrl = saraminUrlNormalizer.normalize(url);

        URI uri = URI.create(normalizedUrl);

        if (!robotsTxtChecker.isAllowed(uri)) {
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.DISALLOWED_BY_ROBOTS,
                "Blocked by robots.txt"
            );
        }

        String html;
        try {
            html = webClient.get()
                .uri(normalizedUrl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    resp -> Mono.error(new RuntimeException("4xx")))
                .onStatus(HttpStatusCode::is5xxServerError,
                    resp -> Mono.error(new RuntimeException("5xx")))
                .bodyToMono(String.class)
                .block();
        } catch (WebClientResponseException e) {
            log.warn("[JobPosting][ContentReader] HTTP error when fetching url={} status={} body={}",
                url, e.getStatusCode(), e.getResponseBodyAsString());


            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return JobPostingContentReadResult.error(
                    JobPostingContentReadStatus.NEED_LOGIN,
                    "Need login or forbidden"
                );
            }
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.UNKNOWN_ERROR,
                "Http error: " + e.getStatusCode()
            );
        } catch (Exception e) {
            log.warn("[JobPosting][ContentReader] Exception when fetching url={}", url, e);
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.UNKNOWN_ERROR,
                e.getMessage()
            );
        }

        if (html == null || html.isBlank()) {
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.UNKNOWN_ERROR,
                "Empty HTML"
            );
        }

        Document doc = Jsoup.parse(html);
        String title = doc.title();

        Element body = doc.body();
        if (body == null) {
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.UNKNOWN_ERROR,
                "No body tag"
            );
        }

        String bodyText = body.text();

        if (bodyText.length() < 100) {
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.JS_RENDERING_NOT_SUPPORTED,
                "Too short body text, probably needs JS rendering"
            );
        }
        if (log.isDebugEnabled()) {
            String preview = bodyText.length() > 1000
                ? bodyText.substring(0, 1000) + "..."
                : bodyText;

            log.debug("[JobPosting][ContentReader] url={} title={}", url, title);
            log.debug("[JobPosting][ContentReader] extracted body (len={}): {}",
                bodyText.length(), preview);
        }

        JobPostingContent contentDto = JobPostingContent.builder()
            .url(url)
            .domain(uri.getHost())
            .title(title)
            .bodyText(bodyText)
            .build();

        return JobPostingContentReadResult.success(contentDto);
    }
}
