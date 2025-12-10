package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ApplicationPolicyValidatorTest extends TestDoubleSupport {

    @InjectMocks
    private ApplicationPolicyValidator applicationPolicyValidator;

    @Test
    void 지원서_생성_시_기타_전형_진행_중일_때만_메모_작성_가능하다() {
        // given
        var newApplicationInfo = new NewApplicationInfo(
                ApplicationStatus.WAITING_FOR_ETC,
                ApplicationMethod.EMAIL,
                "memo",
                LocalDate.now(),
                LocalDate.now()
        );
        // when & then
        applicationPolicyValidator.validateMemoRule(newApplicationInfo);
    }

    @Test
    void 지원서_생성_시_기타_전형_진행_중_이지_않을_떄_메모_작성_시_예외를_반환한다() {
        // given
        var newApplicationInfo = new NewApplicationInfo(
                ApplicationStatus.DOCUMENT_PREPARING,
                ApplicationMethod.EMAIL,
                "memo",
                LocalDate.now(),
                LocalDate.now()
        );

        // when & then
        assertThatThrownBy(() -> applicationPolicyValidator.validateMemoRule(newApplicationInfo))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_MEMO_RULE.getMessage());
    }

}