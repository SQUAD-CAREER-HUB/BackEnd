package org.squad.careerhub.domain.application.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;

class JobPostingRequestTest {

    @Test
    void toNewJobPosting으로_NewJobPosting을_생성한다() {
        // given
        JobPostingRequest request = JobPostingRequest.builder()
                .jobPostingUrl("https://careers.example.com/job/123")
                .company("Example Corp")
                .position("Backend Developer")
                .jobLocation("Seoul, Korea")
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting).isNotNull();
        assertThat(newJobPosting.jobPostingUrl()).isEqualTo("https://careers.example.com/job/123");
        assertThat(newJobPosting.company()).isEqualTo("Example Corp");
        assertThat(newJobPosting.position()).isEqualTo("Backend Developer");
        assertThat(newJobPosting.jobLocation()).isEqualTo("Seoul, Korea");
    }

    @Test
    void jobPostingUrl이_null인_경우_NewJobPosting을_생성한다() {
        // given - 직접 입력 시 URL은 null
        JobPostingRequest request = JobPostingRequest.builder()
                .jobPostingUrl(null)
                .company("Test Company")
                .position("Software Engineer")
                .jobLocation("Busan")
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting.jobPostingUrl()).isNull();
        assertThat(newJobPosting.company()).isEqualTo("Test Company");
    }

    @Test
    void jobLocation이_null인_경우_NewJobPosting을_생성한다() {
        // given
        JobPostingRequest request = JobPostingRequest.builder()
                .jobPostingUrl("https://example.com")
                .company("Remote Company")
                .position("Full Stack Developer")
                .jobLocation(null)
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting.jobLocation()).isNull();
        assertThat(newJobPosting.company()).isEqualTo("Remote Company");
    }

    @Test
    void 모든_필드가_채워진_NewJobPosting을_생성한다() {
        // given
        String url = "https://www.wanted.co.kr/wd/12345";
        String company = "원티드랩";
        String position = "Java 백엔드 개발자";
        String location = "서울특별시 송파구";

        JobPostingRequest request = JobPostingRequest.builder()
                .jobPostingUrl(url)
                .company(company)
                .position(position)
                .jobLocation(location)
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting).extracting(
                NewJobPosting::jobPostingUrl,
                NewJobPosting::company,
                NewJobPosting::position,
                NewJobPosting::jobLocation
        ).containsExactly(url, company, position, location);
    }

    @Test
    void 빌더로_부분적으로_필드를_설정한_객체를_생성한다() {
        // given & when
        JobPostingRequest request = JobPostingRequest.builder()
                .company("Test Corp")
                .position("Developer")
                .build();

        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting.company()).isEqualTo("Test Corp");
        assertThat(newJobPosting.position()).isEqualTo("Developer");
        assertThat(newJobPosting.jobPostingUrl()).isNull();
        assertThat(newJobPosting.jobLocation()).isNull();
    }

    @Test
    void 긴_URL을_가진_채용공고_요청을_변환한다() {
        // given
        String longUrl = "https://www.example.com/careers/jobs/positions/backend/senior-developer/full-time/seoul?ref=github&source=external";
        JobPostingRequest request = JobPostingRequest.builder()
                .jobPostingUrl(longUrl)
                .company("Company")
                .position("Position")
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting.jobPostingUrl()).isEqualTo(longUrl);
    }

    @Test
    void 특수문자가_포함된_회사명과_포지션으로_변환한다() {
        // given
        JobPostingRequest request = JobPostingRequest.builder()
                .company("Company (주)")
                .position("백엔드 개발자 / Backend Developer")
                .jobLocation("서울 · 판교")
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting.company()).isEqualTo("Company (주)");
        assertThat(newJobPosting.position()).isEqualTo("백엔드 개발자 / Backend Developer");
        assertThat(newJobPosting.jobLocation()).isEqualTo("서울 · 판교");
    }

}