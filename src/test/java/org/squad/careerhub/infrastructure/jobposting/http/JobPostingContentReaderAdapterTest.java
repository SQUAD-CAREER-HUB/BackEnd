package org.squad.careerhub.infrastructure.jobposting.http;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.infrastructure.jobposting.http.util.RobotsTxtChecker;
import org.squad.careerhub.infrastructure.jobposting.http.util.SaraminUrlNormalizer;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class JobPostingContentReaderAdapterTest {

    WebClient stubWebClient;

    @Mock
    RobotsTxtChecker robotsTxtChecker;

    @Mock
    SaraminUrlNormalizer saraminUrlNormalizer;

    @Mock
    RallitJobPostingApiClient rallitJobPostingApiClient;

    JobPostingContentReaderAdapter adapter;

    @BeforeEach
    void setUp() {
        String html = "<html><head><title>그린카 채용</title></head>"
            + "<body>그린카 채용공고 입니다. 주요업무 ...</body></html>";

        ClientResponse response = ClientResponse.create(HttpStatus.OK)
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html)
            .build();

        stubWebClient = WebClient.builder()
            .exchangeFunction(req -> Mono.just(response))
            .build();

        adapter = new JobPostingContentReaderAdapter(
            stubWebClient, robotsTxtChecker, saraminUrlNormalizer, rallitJobPostingApiClient
        );
    }

    @Test
    void robots에서_차단되면_DISALLOWED_BY_ROBOTS_반환한다() {
        // given
        String url = "https://www.jobkorea.co.kr/Recruit/12345";
        given(saraminUrlNormalizer.normalize(url)).willReturn(url);
        given(robotsTxtChecker.isAllowed(any())).willReturn(false);

        // when
        JobPostingContentReadResult result = adapter.read(url);

        // then
        assertThat(result.status()).isEqualTo(JobPostingContentReadStatus.DISALLOWED_BY_ROBOTS);
    }

    @Test
    void rallit_도메인은_RallitApiClient_우선_사용한다() {
        String url = "https://www.rallit.com/positions/3974/...";
        String normalized = url;

        given(saraminUrlNormalizer.normalize(url)).willReturn(normalized);
        given(robotsTxtChecker.isAllowed(any())).willReturn(true);

        JobPostingContent rallitContent = JobPostingContent.builder()
            .url(url)
            .domain("www.rallit.com")
            .title("멀티플랫폼 앱 개발자 - 룰루랩")
            .bodyText("Rallit API에서 온 본문")
            .build();

        given(rallitJobPostingApiClient.fetchPosition(any(), eq(url)))
            .willReturn(Optional.of(rallitContent));

        // when
        JobPostingContentReadResult result = adapter.read(url);

        // then
        assertThat(result.status()).isEqualTo(JobPostingContentReadStatus.SUCCESS);
        assertThat(result.content().bodyText()).contains("Rallit API에서 온 본문");
    }
}

