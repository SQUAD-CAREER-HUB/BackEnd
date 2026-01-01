package org.squad.careerhub.infrastructure.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.ControllerTestSupport;

class HealthCheckControllerTest extends ControllerTestSupport {

    @Test
    void Health_Check_API는_정상적으로_응답한다() {
        // when & then
        assertThat(mvcTester.get().uri("/health"))
                .hasStatusOk()
                .apply(print())
                .bodyJson()
                .hasPathSatisfying("$.status", status -> assertThat(status).isEqualTo("OK"))
                .hasPathSatisfying("$.message", message -> assertThat(message).isEqualTo("Application is running"));
    }

    @Test
    void Root_경로는_API_서버_정보를_반환한다() {
        // when & then
        assertThat(mvcTester.get().uri("/"))
                .hasStatusOk()
                .apply(print())
                .bodyJson()
                .hasPathSatisfying("$.status", status -> assertThat(status).isEqualTo("OK"))
                .hasPathSatisfying("$.message", message -> assertThat(message).isEqualTo("CareerHub API Server"));
    }

    @Test
    void HealthCheckResponse는_올바른_값을_가진다() {
        // given
        var status = "OK";
        var message = "Test message";

        // when
        var response = new HealthCheckResponse(status, message);

        // then
        assertThat(response.status()).isEqualTo(status);
        assertThat(response.message()).isEqualTo(message);
    }
}

