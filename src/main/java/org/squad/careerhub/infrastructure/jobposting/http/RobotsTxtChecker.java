package org.squad.careerhub.infrastructure.jobposting.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RobotsTxtChecker {

    private final WebClient webClient;

    public boolean isAllowed(URI uri) {
        String robotsUrl = uri.getScheme() + "://" + uri.getHost() + "/robots.txt";

        String robotsTxt = fetchRobotsTxt(robotsUrl);
        if (robotsTxt == null) {
            // robots.txt 없으면 일단 허용 (보수적으로 가고 싶으면 false로 바꿔도 됨)
            return true;
        }

        List<String> disallowsForAll = parseDisallowsForUserAgentStar(robotsTxt);
        String path = uri.getPath();

        // path가 Disallow 패턴에 매칭되면 크롤링 금지
        return disallowsForAll.stream()
            .noneMatch(path::startsWith);
    }

    private String fetchRobotsTxt(String robotsUrl) {
        try {
            return webClient.get()
                .uri(robotsUrl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    resp -> Mono.error(new RuntimeException("4xx")))
                .onStatus(HttpStatusCode::is5xxServerError,
                    resp -> Mono.error(new RuntimeException("5xx")))
                .bodyToMono(String.class)
                .block();
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseDisallowsForUserAgentStar(String robotsTxt) {
        String[] lines = robotsTxt.split("\\R");
        List<String> disallows = new ArrayList<>();

        boolean inStarSection = false;

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.toLowerCase().startsWith("user-agent:")) {
                String ua = line.substring("user-agent:".length()).trim();
                inStarSection = "*".equals(ua);
                continue;
            }

            if (!inStarSection) {
                continue;
            }

            if (line.toLowerCase().startsWith("disallow:")) {
                String value = line.substring("disallow:".length()).trim();
                if (!value.isEmpty()) {
                    disallows.add(value);
                }
            }

            // 필요하면 Allow: 도 처리 가능
        }

        return disallows;
    }
}

