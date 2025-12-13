package org.squad.careerhub.infrastructure.jobposting.http;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import reactor.core.publisher.Mono;

class RallitJobPostingApiClientTest {
    private ObjectMapper objectMapper;
    private WebClient stubWebClient;
    private RallitJobPostingApiClient client;

    @BeforeEach
    void setUp() throws IOException {

        objectMapper = new ObjectMapper();
        String json = Files.readString(
            Path.of("src/test/java/org/squad/careerhub/infrastructure/jobposting/http/resources/position_3974.json")
        );

        ClientResponse response = ClientResponse
            .create(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(json)
            .build();

        stubWebClient = WebClient.builder()
            .exchangeFunction(req -> Mono.just(response))
            .build();

        client = new RallitJobPostingApiClient(stubWebClient, objectMapper);
    }

    @Test
    void rallit_포지션_API_파싱을_성공한다() throws Exception {
        // given
        URI uri = new URI("https://www.rallit.com/positions/3974/멀티플랫폼-앱-개발자-android-flutter");
        String url = uri.toString();

        // when
        Optional<JobPostingContent> result = client.fetchPosition(uri, url);

        // then
        assertThat(result).isPresent();
        JobPostingContent content = result.get();

        assertThat(content.domain()).isEqualTo("www.rallit.com");
        assertThat(content.title()).contains("멀티플랫폼 앱 개발자");
        assertThat(content.bodyText()).contains("안드로이드 앱 개발 또는 Flutter 개발 5년 이상");
    }
}
