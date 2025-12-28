package org.squad.careerhub.domain.jobposting.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.jobposting.enums.JobPostingContentReadStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class JobPostingValidatorTest {

    private final JobPostingValidator validator = new JobPostingValidator();

    @Nested
    class ValidateJobPostingUrl {

        @Test
        void 지원하는_도메인의_http_https_URL은_검증을_통과한다() {
            // wanted
            assertThatCode(() ->
                validator.validateJobPostingUrl("https://www.wanted.co.kr/wd/12345")
            ).doesNotThrowAnyException();

            // saramin
            assertThatCode(() ->
                validator.validateJobPostingUrl("https://www.saramin.co.kr/zf_user/jobs/relay/view?rec_idx=123")
            ).doesNotThrowAnyException();

            // jobkorea
            assertThatCode(() ->
                validator.validateJobPostingUrl("https://www.jobkorea.co.kr/Recruit/GI_Read/12345")
            ).doesNotThrowAnyException();

            // rallit
            assertThatCode(() ->
                validator.validateJobPostingUrl("https://www.rallit.com/positions/3974/멀티플랫폼-앱-개발자")
            ).doesNotThrowAnyException();
        }

        @Test
        void URL이_null이거나_빈문자열이면_BAD_REQUEST_예외를_던진다() {
            assertThatThrownBy(() -> validator.validateJobPostingUrl(null))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.URL_ERROR);

            assertThatThrownBy(() -> validator.validateJobPostingUrl(" "))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.URL_ERROR);
        }

        @Test
        void http_https가_아닌_스킴이면_URL_ERROR_예외를_던진다() {
            String url = "ftp://www.wanted.co.kr/wd/12345";

            assertThatThrownBy(() -> validator.validateJobPostingUrl(url))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.URL_ERROR);
        }

        @Test
        void 지원하지_않는_도메인이면_URL_ERROR_예외를_던진다() {
            String url = "https://www.google.com/search?q=job";

            assertThatThrownBy(() -> validator.validateJobPostingUrl(url))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.URL_ERROR);
        }
    }

    @Nested
    class ValidateReadResult {

        @Test
        void SUCCESS_상태면_예외없이_통과한다() {
            JobPostingContentReadResult result = JobPostingContentReadResult.success(null);

            assertThatCode(() -> validator.validateReadResult(result))
                .doesNotThrowAnyException();
        }

        @Test
        void DISALLOWED_BY_ROBOTS면_JOB_POSTING_ROBOTS_BLOCKED_예외를_던진다() {
            JobPostingContentReadResult result =
                JobPostingContentReadResult.error(
                    JobPostingContentReadStatus.DISALLOWED_BY_ROBOTS,
                    "Blocked by robots.txt"
                );

            assertThatThrownBy(() -> validator.validateReadResult(result))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.JOB_POSTING_ROBOTS_BLOCKED);
        }

        @Test
        void NEED_LOGIN이면_JOB_POSTING_NEED_LOGIN_예외를_던진다() {
            JobPostingContentReadResult result =
                JobPostingContentReadResult.error(
                    JobPostingContentReadStatus.NEED_LOGIN,
                    "Login required"
                );

            assertThatThrownBy(() -> validator.validateReadResult(result))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.JOB_POSTING_NEED_LOGIN);
        }

        @Test
        void JS_RENDERING_NOT_SUPPORTED면_JOB_POSTING_JS_RENDER_REQUIRED_예외를_던진다() {
            JobPostingContentReadResult result =
                JobPostingContentReadResult.error(
                    JobPostingContentReadStatus.JS_RENDERING_NOT_SUPPORTED,
                    "Needs JS rendering"
                );

            assertThatThrownBy(() -> validator.validateReadResult(result))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.JOB_POSTING_JS_RENDER_REQUIRED);
        }

        @Test
        void UNKNOWN_ERROR면_JOB_POSTING_READ_FAILED_예외를_던진다() {
            JobPostingContentReadResult result =
                JobPostingContentReadResult.error(
                    JobPostingContentReadStatus.UNKNOWN_ERROR,
                    "Unknown error"
                );

            assertThatThrownBy(() -> validator.validateReadResult(result))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.JOB_POSTING_READ_FAILED);
        }
    }
}