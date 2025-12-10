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
import org.squad.careerhub.domain.community.interviewquestion.service.dto.UpdateReviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.community.interviewreview.service.dto.UpdateInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

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

        var interviewQuestionList = interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId,
                EntityStatus.ACTIVE);
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

        var interviewQuestionList = interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId,
                EntityStatus.ACTIVE);
        assertThat(interviewQuestionList).isEmpty();
    }

    @Test
    void 면접_후기와_질문을_함께_수정한다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of("기존 질문1", "기존 질문2"));

        List<InterviewQuestion> existingQuestions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        var updateReview = new UpdateInterviewReview(
                "수정된 회사",
                "수정된 포지션",
                "수정된 면접 유형",
                "수정된 내용"
        );

        List<UpdateReviewQuestion> updateQuestions = List.of(
                new UpdateReviewQuestion(existingQuestions.getFirst().getId(), "수정된 질문1"),
                new UpdateReviewQuestion(null, "새로운 질문")
        );

        // when
        interviewReviewService.update(updateReview, updateQuestions, reviewId, member.getId());

        // then
        var updatedReview = interviewReviewJpaRepository.findById(reviewId).orElseThrow();
        assertThat(updatedReview)
                .extracting(
                        InterviewReview::getCompany,
                        InterviewReview::getPosition,
                        InterviewReview::getInterviewType,
                        InterviewReview::getContent
                )
                .containsExactly("수정된 회사", "수정된 포지션", "수정된 면접 유형", "수정된 내용");

        List<InterviewQuestion> activeQuestions = interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        assertThat(activeQuestions).hasSize(2);
        assertThat(activeQuestions)
                .extracting(InterviewQuestion::getQuestion)
                .containsExactlyInAnyOrder("수정된 질문1", "새로운 질문");
    }

    @Test
    void 요청에_없는_기존_질문은_삭제된다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of("삭제될 질문1", "삭제될 질문2", "유지될 질문"));

        List<InterviewQuestion> existingQuestions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        var keepQuestionId = existingQuestions.get(2).getId();

        var updateReview = new UpdateInterviewReview(
                "회사",
                "포지션",
                "면접 유형",
                "내용"
        );

        List<UpdateReviewQuestion> updateQuestions = List.of(
                new UpdateReviewQuestion(keepQuestionId, "유지될 질문 수정")
        );

        // when
        interviewReviewService.update(updateReview, updateQuestions, reviewId, member.getId());

        // then
        List<InterviewQuestion> activeQuestions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);
        assertThat(activeQuestions).hasSize(1);
        assertThat(activeQuestions.getFirst().getQuestion()).isEqualTo("유지될 질문 수정");
    }

    @Test
    void 작성자가_아닌_사용자가_수정시_예외가_발생한다() {
        // given
        var author = createMember();
        var otherMember = memberJpaRepository.save(Member.create(
                "other@email.com", SocialProvider.GOOGLE, "otherSocialId", "다른사람", "profile.jpg"
        ));

        var reviewId = createReviewWithQuestions(author, List.of("질문1"));

        var updateReview = new UpdateInterviewReview(
                "수정 시도",
                "포지션",
                "면접 유형",
                "내용"
        );

        // when & then
        assertThatThrownBy(() -> interviewReviewService.update(updateReview, List.of(), reviewId, otherMember.getId()))
                .isInstanceOf(CareerHubException.class)
                .hasFieldOrPropertyWithValue("errorStatus", ErrorStatus.FORBIDDEN_MODIFY);
    }

    @Test
    void 존재하지_않는_리뷰_수정시_예외가_발생한다() {
        // given
        var member = createMember();
        var nonExistentReviewId = 999999L;

        var updateReview = new UpdateInterviewReview(
                "회사",
                "포지션",
                "면접 유형",
                "내용"
        );

        // when & then
        assertThatThrownBy(() -> interviewReviewService.update(updateReview, List.of(), nonExistentReviewId, member.getId()))
                .isInstanceOf(CareerHubException.class)
                .hasFieldOrPropertyWithValue("errorStatus", ErrorStatus.NOT_FOUND_REVIEW);
    }

    @Test
    void 빈_질문_목록으로_수정하면_기존_질문이_모두_삭제된다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of("질문1", "질문2"));

        var updateReview = new UpdateInterviewReview(
                "회사", "포지션", "면접 유형", "내용"
        );

        // when
        interviewReviewService.update(updateReview, List.of(), reviewId, member.getId());

        // then
        List<InterviewQuestion> activeQuestions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);
        assertThat(activeQuestions).isEmpty();
    }

    @Test
    void 새로운_질문만_추가한다() {
        // given
        var member = createMember();
        var reviewId = createReviewWithQuestions(member, List.of("기존 질문1"));

        List<InterviewQuestion> existingQuestions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        var updateReview = new UpdateInterviewReview(
                "회사",
                "포지션",
                "면접 유형",
                "내용"
        );

        List<UpdateReviewQuestion> updateQuestions = List.of(
                new UpdateReviewQuestion(existingQuestions.getFirst().getId(), "기존 질문1"),
                new UpdateReviewQuestion(null, "새로운 질문1"),
                new UpdateReviewQuestion(null, "새로운 질문2")
        );

        // when
        interviewReviewService.update(updateReview, updateQuestions, reviewId, member.getId());

        // then
        List<InterviewQuestion> activeQuestions = interviewQuestionJpaRepository
                .findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        assertThat(activeQuestions).hasSize(3);
        assertThat(activeQuestions)
                .extracting(InterviewQuestion::getQuestion)
                .containsExactlyInAnyOrder("기존 질문1", "새로운 질문1", "새로운 질문2");
    }

    private Member createMember() {
        return memberJpaRepository.save(Member.create(
                "test@email.com",
                SocialProvider.KAKAO,
                "socialId" + System.nanoTime(),
                "테스터",
                "profile.jpg"
        ));
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