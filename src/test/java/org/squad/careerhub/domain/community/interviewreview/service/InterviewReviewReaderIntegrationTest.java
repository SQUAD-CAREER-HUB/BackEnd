package org.squad.careerhub.domain.community.interviewreview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.InterviewQuestionResponse;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewDetailResponse;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewSummaryResponse;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Transactional
class InterviewReviewReaderIntegrationTest extends IntegrationTestSupport {

    final InterviewReviewReader interviewReviewReader;
    final InterviewReviewJpaRepository interviewReviewJpaRepository;
    final InterviewQuestionJpaRepository interviewQuestionJpaRepository;
    final MemberJpaRepository memberJpaRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(Member.create(
                "test@email.com",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl"
        ));
    }

    @Test
    void 면접_후기_목록을_조회한다() {
        // given
        interviewReviewJpaRepository.save(InterviewReview.create(member, "카카오", "백엔드", "온라인", "내용1"));
        interviewReviewJpaRepository.save(InterviewReview.create(member, "네이버", "프론트엔드", "대면", "내용2"));

        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ReviewSummaryResponse> result = interviewReviewReader.findReviews(null, SortType.NEWEST, cursor);

        // then
        assertThat(result.contents()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 검색어로_면접_후기를_조회한다() {
        // given
        interviewReviewJpaRepository.save(InterviewReview.create(member, "카카오", "백엔드", "온라인", "내용1"));
        interviewReviewJpaRepository.save(InterviewReview.create(member, "네이버", "프론트엔드", "대면", "내용2"));

        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ReviewSummaryResponse> result = interviewReviewReader.findReviews("카카오", SortType.NEWEST, cursor);
        PageResponse<ReviewSummaryResponse> result2 = interviewReviewReader.findReviews("   ", SortType.NEWEST, cursor);
        PageResponse<ReviewSummaryResponse> result3 = interviewReviewReader.findReviews(" 네이버   ", SortType.NEWEST, cursor);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().getFirst().company()).isEqualTo("카카오");

        assertThat(result2.contents()).hasSize(2);

        assertThat(result3.contents()).hasSize(1);
        assertThat(result3.contents().getFirst().company()).isEqualTo("네이버");
    }



    @Test
    void 커서_기반_페이징이_동작한다() {
        // given
        for (int i = 0; i < 15; i++) {
            interviewReviewJpaRepository.save(InterviewReview.create(member, "회사" + i, "포지션", "타입", "내용"));
        }

        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ReviewSummaryResponse> result = interviewReviewReader.findReviews(null, SortType.NEWEST, cursor);

        // then
        assertThat(result.contents()).hasSize(cursor.limit());
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursorId()).isNotNull();
    }

    @Test
    void 다음_페이지를_조회한다() {
        // given
        for (int i = 0; i < 15; i++) {
            interviewReviewJpaRepository.save(InterviewReview.create(member, "회사" + i, "포지션", "타입", "내용"));
        }

        var firstCursor = Cursor.of(null, 10);
        PageResponse<ReviewSummaryResponse> firstPage = interviewReviewReader.findReviews(null, SortType.NEWEST, firstCursor);

        var secondCursor = Cursor.of(firstPage.nextCursorId(), 10);

        // when
        PageResponse<ReviewSummaryResponse> secondPage = interviewReviewReader.findReviews(null, SortType.NEWEST, secondCursor);

        // then
        assertThat(secondPage.contents()).hasSize(5);
        assertThat(secondPage.hasNext()).isFalse();
    }

    @Test
    void 조회_결과가_없으면_빈_리스트를_반환한다() {
        // given
        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ReviewSummaryResponse> response = interviewReviewReader.findReviews(null, SortType.NEWEST, cursor);

        // then
        assertThat(response.contents()).isEmpty();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursorId()).isNull();
    }

    @Test
    void 면접_후기를_상세조회_한다() {
        // given
        var savedReview = interviewReviewJpaRepository.save(InterviewReview.create(
                        member,
                        "카카오",
                        "백엔드",
                        "온라인",
                        "내용1"
                )
        );
        var savedQuestion = interviewQuestionJpaRepository.save(InterviewQuestion.create(savedReview, "질문1"));

        // when
        ReviewDetailResponse response = interviewReviewReader.findReview(savedReview.getId(), member.getId());

        // then
        assertThat(response).isNotNull()
                .extracting(
                        ReviewDetailResponse::reviewId,
                        ReviewDetailResponse::company,
                        ReviewDetailResponse::position,
                        ReviewDetailResponse::interviewType,
                        ReviewDetailResponse::content
                ).containsExactly(
                        savedReview.getId(),
                        "카카오",
                        "백엔드",
                        "온라인",
                        "내용1"
                );

        assertThat(response.interviewQuestions()).hasSize(1);
        assertThat(response.interviewQuestions().getFirst())
                .extracting(
                        InterviewQuestionResponse::questionId,
                        InterviewQuestionResponse::question
                ).containsExactly(
                        savedQuestion.getId(),
                        savedQuestion.getQuestion()
                );
    }

    @Test
    void 면접_후기가_존재하지_않을_경우_예외를_반환한다() {
        // when
        long invalidReviewId = -999L;
        assertThatThrownBy(() -> interviewReviewReader.findReview(invalidReviewId, member.getId()))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_REVIEW.getMessage());

    }

}