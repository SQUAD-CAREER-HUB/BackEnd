package org.squad.careerhub.domain.community.interviewreview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Transactional
class InterviewReviewManagerIntegrationTest extends IntegrationTestSupport {

    final InterviewReviewManager interviewReviewManager;
    final InterviewReviewService interviewReviewService;
    final InterviewReviewJpaRepository interviewReviewJpaRepository;
    final InterviewQuestionJpaRepository interviewQuestionJpaRepository;
    final MemberJpaRepository memberJpaRepository;

    @Test
    void 면접_후기와_질문을_함께_삭제한다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of("질문1", "질문2", "질문3"));

        // when
        interviewReviewManager.deleteReview(reviewId, member.getId());

        // then
        var deletedReview = interviewReviewJpaRepository.findById(reviewId).orElseThrow();
        assertThat(deletedReview.isDeleted()).isTrue();

        List<InterviewQuestion> questions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);
        assertThat(questions).isEmpty();
    }

    @Test
    void 질문이_없는_면접_후기도_삭제할_수_있다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of());

        // when
        interviewReviewManager.deleteReview(reviewId, member.getId());

        // then
        var deletedReview = interviewReviewJpaRepository.findById(reviewId).orElseThrow();
        assertThat(deletedReview.isDeleted()).isTrue();
    }

    @Test
    void 작성자가_아닌_사용자가_삭제시_예외가_발생한다() {
        // given
        var author = createMember();
        var otherMember = memberJpaRepository.save(Member.create(
                "other@email.com", SocialProvider.GOOGLE, "otherSocialId", "다른사람", "profile.jpg"
        ));

        var reviewId = createReviewWithQuestions(author, List.of("질문1"));

        // when & then
        assertThatThrownBy(() -> interviewReviewManager.deleteReview(reviewId, otherMember.getId()))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FORBIDDEN_DELETE.getMessage());

        // 삭제되지 않았는지 확인
        var review = interviewReviewJpaRepository.findById(reviewId).orElseThrow();
        assertThat(review.isActive()).isTrue();
    }

    @Test
    void 존재하지_않는_리뷰_삭제시_예외가_발생한다() {
        // given
        var member = createMember();
        var nonExistentReviewId = 999999L;

        // when & then
        assertThatThrownBy(() -> interviewReviewManager.deleteReview(nonExistentReviewId, member.getId()))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_REVIEW.getMessage());
    }

    @Test
    void 이미_삭제된_리뷰는_다시_삭제할_수_없다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of("질문1"));

        // 먼저 삭제
        interviewReviewManager.deleteReview(reviewId, member.getId());

        // when & then
        assertThatThrownBy(() -> interviewReviewManager.deleteReview(reviewId, member.getId()))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_REVIEW.getMessage());
    }

    private Member createMember() {
        return memberJpaRepository.save(MemberFixture.createMember());
    }

    private Long createReviewWithQuestions(Member member, List<String> questions) {
        NewInterviewReview newReview = new NewInterviewReview(
                "카카오",
                "백엔드",
                "기술면접",
                "면접 후기"
        );
        return interviewReviewService.createReview(newReview, questions, member.getId());
    }

}