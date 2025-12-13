package org.squad.careerhub.infrastructure.jobposting.http;

import java.net.URI;
import java.time.Duration;
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
import org.squad.careerhub.infrastructure.jobposting.http.util.RobotsTxtChecker;
import org.squad.careerhub.infrastructure.jobposting.http.util.SaraminUrlNormalizer;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobPostingContentReaderAdapter implements JobPostingContentReaderPort {

    private final WebClient webClient;
    private final RobotsTxtChecker robotsTxtChecker;
    private final SaraminUrlNormalizer saraminUrlNormalizer;
    private final RallitJobPostingApiClient rallitJobPostingApiClient;

    @Override
    public JobPostingContentReadResult read(String url) {

        String normalizedUrl = saraminUrlNormalizer.normalize(url);
        URI uri = URI.create(normalizedUrl);

        // 1) robots.txt 체크
        if (!robotsTxtChecker.isAllowed(uri)) {
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.DISALLOWED_BY_ROBOTS,
                "Blocked by robots.txt"
            );
        }
        // Rallit면 전용 파서 먼저 시도
        var rallitResult = rallitJobPostingApiClient.fetchPosition(uri, url);

        log.debug("[JobPosting][ContentReader] rallit parsed={}", rallitResult.isPresent());

        if (rallitResult.isPresent()) {
            return JobPostingContentReadResult.success(rallitResult.get());
        }

        // 2) HTML GET
        String html = fetchHtml(normalizedUrl);
        if (html == null || html.isBlank()) {
            return JobPostingContentReadResult.error(
                JobPostingContentReadStatus.UNKNOWN_ERROR,
                "Empty HTML"
            );
        }

        // 3) generic HTML → body.text() 기반 파싱
        return parseGenericHtml(uri, url, html);
    }

    private String fetchHtml(String normalizedUrl) {
        try {
            return webClient.get()
                .uri(normalizedUrl)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> resp.createException().flatMap(Mono::error))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();
        } catch (WebClientResponseException e) {
            log.warn("[JobPosting][ContentReader] HTTP error when fetching url={} status={} body={}",
                normalizedUrl, e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                // 필요하다면 JobPostingContentReadStatus.NEED_LOGIN으로 바로 매핑하도록 변경 가능
                return null;
            }
            return null;
        } catch (Exception e) {
            log.warn("[JobPosting][ContentReader] Exception when fetching url={}", normalizedUrl, e);
            return null;
        }
    }

    private JobPostingContentReadResult parseGenericHtml(URI uri, String url, String html) {
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