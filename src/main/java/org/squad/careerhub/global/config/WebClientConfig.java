package org.squad.careerhub.global.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    HttpClient httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(15))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/120.0.0.0 Safari/537.36")
            .defaultHeader(HttpHeaders.ACCEPT,
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .build();
    }
}
