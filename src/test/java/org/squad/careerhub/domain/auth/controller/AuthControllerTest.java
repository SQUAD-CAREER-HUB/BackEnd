package org.squad.careerhub.domain.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.auth.controller.dto.ReissueTokenRequest;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;

class AuthControllerTest extends ControllerTestSupport {


    @Test
    void 토큰을_재발급한다() throws JsonProcessingException {
        // given
        var request = new ReissueTokenRequest("refresh-token-value");
        var requestJson = objectMapper.writeValueAsString(request);

        var tokenResponse = new TokenResponse("refresh-token-value", "refresh-token-value");
        given(authService.reissue("refresh-token-value")).willReturn(tokenResponse);

        // when & then
        assertThat(mvcTester.post().uri("/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.accessToken", v -> v.assertThat().isEqualTo(tokenResponse.accessToken()))
                .hasPathSatisfying("$.refreshToken", v -> v.assertThat().isEqualTo(tokenResponse.refreshToken()));
    }

}