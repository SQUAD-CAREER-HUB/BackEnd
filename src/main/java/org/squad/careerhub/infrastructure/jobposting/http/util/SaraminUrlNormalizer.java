package org.squad.careerhub.infrastructure.jobposting.http.util;

import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SaraminUrlNormalizer {

    public String normalize(String url) {
        if (url == null) return null;
        String trimmed = url.trim();
        if (trimmed.isEmpty()) return url;

        URI uri;
        try {
            uri = URI.create(trimmed);
        } catch (IllegalArgumentException e) {
            // URL 형식이 깨진 경우 → 그냥 원본 유지(또는 null/예외 변환 정책)
            return url;
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (scheme == null || host == null) {
            return url;
        }

        if (!host.contains("saramin.co.kr")) {
            return url;
        }

        String path = uri.getPath();

        if (path.startsWith("/zf_user/jobs/relay/view-detail")) {
            return url;
        }

        // 리스트/중계 view -> detail 로 변환
        if (path.startsWith("/zf_user/jobs/relay/view")) {
            var components = UriComponentsBuilder.fromUri(uri).build();
            String recIdx = components.getQueryParams().getFirst("rec_idx");

            if (recIdx == null || recIdx.isBlank()) {
                // rec_idx 없으면 변환 불가 → 그냥 원본 URL 사용
                return url;
            }

            // https://www.saramin.co.kr/zf_user/jobs/relay/view-detail?rec_idx=52469752
            return UriComponentsBuilder
                .fromUriString(uri.getScheme() + "://" + host)
                .path("/zf_user/jobs/relay/view-detail")
                .queryParam("rec_idx", recIdx)
                .build(true)
                .toUriString();
        }

        return url;
    }
}