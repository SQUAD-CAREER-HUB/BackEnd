package org.squad.careerhub.infrastructure.jobposting.http.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SaraminUrlNormalizerTest {

    private final SaraminUrlNormalizer normalizer = new SaraminUrlNormalizer();

    @Test
    void 사람인_도메인이_아니면_URL을_변경하지_않는다() {
        // given
        String url = "https://www.naver.com/job/detail?rec_idx=123";

        // when
        String normalized = normalizer.normalize(url);

        // then
        assertThat(normalized).isEqualTo(url);
    }

    @Test
    void 이미_view_detail_URL이면_그대로_반환한다() {
        // given
        String url = "https://www.saramin.co.kr/zf_user/jobs/relay/view-detail?rec_idx=52469752";

        // when
        String normalized = normalizer.normalize(url);

        // then
        assertThat(normalized).isEqualTo(url);
    }

    @Test
    void relay_view_URL_rec_idx가_있는_경우_view_detail_URL로_변환한다() {
        // given
        String url = "https://www.saramin.co.kr/zf_user/jobs/relay/view?rec_idx=52469752&other_param=abc";

        // when
        String normalized = normalizer.normalize(url);

        // then
        assertThat(normalized)
            .isEqualTo("https://www.saramin.co.kr/zf_user/jobs/relay/view-detail?rec_idx=52469752");
    }

    @Test
    void relay_view_URL이지만_rec_idx가_없으면_원본URL을_그대로_반환한다() {
        // given
        String url = "https://www.saramin.co.kr/zf_user/jobs/relay/view?foo=bar";

        // when
        String normalized = normalizer.normalize(url);

        // then
        assertThat(normalized).isEqualTo(url);
    }

    @Test
    @DisplayName("사라민 도메인의 다른 path는 변경하지 않는다")
    void 사람인_도메인의_다른_path는_변경하지_않는다() {
        // given
        String url = "https://www.saramin.co.kr/zf_user/jobs/list?searchType=search&searchword=백엔드";

        // when
        String normalized = normalizer.normalize(url);

        // then
        assertThat(normalized).isEqualTo(url);
    }
}
