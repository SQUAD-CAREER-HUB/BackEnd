package org.squad.careerhub.domain.community.interviewreview.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;

@RequiredArgsConstructor
@Transactional
class InterviewReviewServiceIntegrationTest extends IntegrationTestSupport {

    final InterviewReviewService interviewReviewService;
    final InterviewReviewJpaRepository interviewReviewJpaRepository;
    final InterviewQuestionJpaRepository interviewQuestionJpaRepository;
    final MemberJpaRepository memberJpaRepository;

    @Test
    void 면접_후기를_작성한다() {
        // given
        var member = memberJpaRepository.save(Member.create(
                "email",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl")
        );
        var newReview = new NewInterviewReview(
                "company",
                "position",
                "interviewType",
                "content"
        );
        var interviewQuestions = List.of(
                "면접 질문 1",
                "면접 질문 2",
                "면접 질문 3"
        );

        // when
        var reviewId = interviewReviewService.createReview(newReview, interviewQuestions, member.getId());

        // then
        var interviewReview = interviewReviewJpaRepository.findById(reviewId).orElseThrow();
        assertThat(interviewReview).isNotNull()
                .extracting(
                        InterviewReview::getId,
                        InterviewReview::getCompany,
                        InterviewReview::getPosition,
                        InterviewReview::getInterviewType,
                        InterviewReview::getContent
                ).containsExactly(
                        reviewId,
                        newReview.company(),
                        newReview.position(),
                        newReview.interviewType(),
                        newReview.content()
                );

        var interviewQuestionList = interviewQuestionJpaRepository.findByInterviewReviewId(reviewId);
        assertThat(interviewQuestionList).hasSize(interviewQuestions.size());
    }

    @Test
    void 면접_질문이_없어도_후기를_작성할_수_있다() {
        // given
        var member = memberJpaRepository.save(Member.create(
                "email",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl")
        );
        var newReview = new NewInterviewReview(
                "company",
                "position",
                "interviewType",
                "content"
        );
        var interviewQuestions = List.<String>of();

        // when
        var reviewId = interviewReviewService.createReview(newReview, interviewQuestions, member.getId());

        // then
        var interviewReview = interviewReviewJpaRepository.findById(reviewId).orElseThrow();
        assertThat(interviewReview).isNotNull();

        var interviewQuestionList = interviewQuestionJpaRepository.findByInterviewReviewId(reviewId);
        assertThat(interviewQuestionList).isEmpty();
    }

}