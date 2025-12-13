package org.squad.careerhub.infrastructure.jobposting.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;

@Component
@Slf4j
@RequiredArgsConstructor
public class RallitJobPostingApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public Optional<JobPostingContent> fetchPosition(URI uri, String url) {
        if (uri.getHost() == null || !uri.getHost().contains("rallit.com")) {
            return Optional.empty();
        }

        long positionId = extractRallitIdFromPath(uri.getPath());
        if (positionId <= 0) {
            return Optional.empty();
        }

        String apiUrl = "https://www.rallit.com/client/api/v1/position/" + positionId;

        try {
            String json = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode root = objectMapper.readTree(json);
            JsonNode dataNode = root.path("data");

            if (dataNode.isMissingNode()) {
                log.debug("[Rallit] position data node missing. apiUrl={}", apiUrl);
                return Optional.empty();
            }

            JobPostingContent content = toJobPostingContentFromRallitJson(url, uri, dataNode);
            return Optional.of(content);

        } catch (Exception e) {
            log.warn("[Rallit] API fetch failed. url={}", apiUrl, e);
            return Optional.empty();
        }
    }

    private long extractRallitIdFromPath(String path) {
        if (path == null) {
            return -1;
        }
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.matches("\\d+")) {
                return Long.parseLong(part);
            }
        }
        return -1;
    }

    private JobPostingContent toJobPostingContentFromRallitJson(String url, URI uri, JsonNode node) {
        String title = node.path("title").asText("");
        String companyName = node.path("companyName").asText("");
        String addressMain = normalizeText(node.path("addressMain").asText(""));
        String addressDetail = normalizeText(node.path("addressDetail").asText(""));
        String status = node.path("status").path("name").asText("");

        StringBuilder bodyBuilder = new StringBuilder();

        // 각 HTML 필드를 섹션별로 붙이기
        appendHtmlField(bodyBuilder, "회사 소개", node.path("description").asText(null));
        appendHtmlField(bodyBuilder, "주요 업무", node.path("responsibilities").asText(null));
        appendHtmlField(bodyBuilder, "자격 요건", node.path("basicQualifications").asText(null));
        appendHtmlField(bodyBuilder, "우대 사항", node.path("preferredQualifications").asText(null));
        appendHtmlField(bodyBuilder, "복지 및 혜택", node.path("benefits").asText(null));
        appendHtmlField(bodyBuilder, "추가 안내", node.path("additionalComments").asText(null));

        // 근무지 정보도 하단에 추가
        if (!addressMain.isBlank()) {
            if (!bodyBuilder.isEmpty()) bodyBuilder.append("\n\n");
            bodyBuilder.append("[근무지]\n")
                .append(addressMain);
            if (!addressDetail.isBlank()) {
                bodyBuilder.append(" ").append(addressDetail);
            }
        }

        String bodyText = normalizeBody(bodyBuilder.toString());

        return JobPostingContent.builder()
            .url(url)
            .domain(uri.getHost())
            .title(buildTitle(title, companyName, status))
            .bodyText(bodyText)
            .build();
    }

    private String normalizeBody(String body) {
        if (body == null) return "";

        String t = body;

        // 제로폭 문자, BOM 제거
        t = t.replaceAll("[\\u200B-\\u200D\\uFEFF]", "");
        // nbsp → 일반 공백
        t = t.replace('\u00A0', ' ');

        // 이모지, 특수 심볼 등은 걍 날려버리자 (원하면 더 느슨하게 조정 가능)
        // \p{L}=문자, \p{N}=숫자, \p{P}=구두점, \p{Z}=공백 구분자, \n=줄바꿈
        t = t.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n]", "");

        // 탭/캐리지리턴 등 → 공백 하나로
        t = t.replaceAll("[ \\t\\x0B\\f\\r]+", " ");

        // 모든 개행을 공백으로 통일
        t = t.replaceAll("\\n+", " ");

        // 공백 여러 개 → 하나
        t = t.replaceAll("\\s+", " ");


        return t.trim();
    }

    /**
     * 주소 같이 짧은 텍스트용
     */
    private String normalizeText(String text) {
        if (text == null) return "";
        String t = text
            .replaceAll("[\\u200B-\\u200D\\uFEFF]", "")
            .replace('\u00A0', ' ');
        return t.replaceAll("\\s+", " ").trim();
    }

    /**
     * Rallit가 내려주는 HTML 조각(예: <p>...</p><ul>...</ul>)을
     * 섹션 제목 + 플레인 텍스트로 붙여주는 헬퍼
     */
    private void appendHtmlField(StringBuilder sb, String label, String htmlFragment) {
        if (htmlFragment == null || htmlFragment.isBlank()) return;

        // HTML → 텍스트
        String text = Jsoup.parse(htmlFragment).text();
        text = normalizeText(text);

        if (text.isBlank()) return;

        if (!sb.isEmpty()) {
            sb.append("\n\n");
        }
        sb.append("[").append(label).append("]\n");
        sb.append(text);
    }

    private String buildTitle(String title, String companyName, String status) {
        StringBuilder sb = new StringBuilder();
        if (!title.isBlank()) sb.append(title);
        if (!companyName.isBlank()) {
            if (!sb.isEmpty()) sb.append(" - ");
            sb.append(companyName);
        }
        if (!status.isBlank()) {
            if (!sb.isEmpty()) sb.append(" (").append(status).append(")");
        }
        return sb.toString();
    }
}
