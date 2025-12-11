package org.squad.careerhub.domain.community.interviewquestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.UpdateReviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@ExtendWith(MockitoExtension.class)
class InterviewQuestionManagerUnitTest {

    @InjectMocks
    private InterviewQuestionManager interviewQuestionManager;

    @Mock
    private InterviewQuestionJpaRepository interviewQuestionJpaRepository;

    private InterviewReview review;
    private Member author;

    @BeforeEach
    void setUp() {
        author = Member.create("test@test.com", SocialProvider.GOOGLE, "socialId123", "테스터", "profile.jpg");
        ReflectionTestUtils.setField(author, "id", 1L);

        review = InterviewReview.create(author, "카카오", "백엔드", "기술면접", "면접 후기 내용");
        ReflectionTestUtils.setField(review, "id", 1L);
    }

    @Test
    void 요청에_없는_기존_질문을_삭제한다() {
        // given
        var reviewId = 1L;

        var existingQuestion1 = createQuestion(1L, "기존 질문1");
        var existingQuestion2 = createQuestion(2L, "기존 질문2");

        given(interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE))
                .willReturn(List.of(existingQuestion1, existingQuestion2));

        // 질문1만 유지, 질문2는 삭제
        List<UpdateReviewQuestion> requests = List.of(
                new UpdateReviewQuestion(1L, "기존 질문1 수정")
        );

        // when
        interviewQuestionManager.updateQuestions(requests, reviewId, review);

        // then
        assertThat(existingQuestion1.isDeleted()).isFalse();
        assertThat(existingQuestion2.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("기존 질문 내용을 수정한다")
    void 기존_질문_내용을_수정한다() {
        // given
        var reviewId = 1L;

        var existingQuestion = createQuestion(1L, "기존 질문");

        given(interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE))
                .willReturn(List.of(existingQuestion));

        List<UpdateReviewQuestion> requests = List.of(
                new UpdateReviewQuestion(1L, "수정된 질문")
        );

        // when
        interviewQuestionManager.updateQuestions(requests, reviewId, review);

        // then
        assertThat(existingQuestion.getQuestion()).isEqualTo("수정된 질문");
    }

    @Test
    @DisplayName("ID가 없는 요청은 새로운 질문을 생성한다")
    void ID가_없는_요청은_새로운_질문을_생성한다() {
        // given
        var reviewId = 1L;

        given(interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE))
                .willReturn(List.of());

        List<UpdateReviewQuestion> requests = List.of(
                new UpdateReviewQuestion(null, "새로운 질문1"),
                new UpdateReviewQuestion(null, "새로운 질문2")
        );

        // when
        interviewQuestionManager.updateQuestions(requests, reviewId, review);

        // then
        ArgumentCaptor<InterviewQuestion> captor = ArgumentCaptor.forClass(InterviewQuestion.class);
        verify(interviewQuestionJpaRepository, times(2)).save(captor.capture());

        List<InterviewQuestion> savedQuestions = captor.getAllValues();
        assertThat(savedQuestions).hasSize(2);
        assertThat(savedQuestions.get(0).getQuestion()).isEqualTo("새로운 질문1");
        assertThat(savedQuestions.get(1).getQuestion()).isEqualTo("새로운 질문2");
    }

    @Test
    @DisplayName("삭제, 수정, 생성이 동시에 처리된다")
    void 삭제_수정_생성이_동시에_처리된다() {
        // given
        var reviewId = 1L;

        var toDelete = createQuestion(1L, "삭제될 질문");
        var toUpdate = createQuestion(2L, "수정될 질문");

        given(interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE))
                .willReturn(List.of(toDelete, toUpdate));

        List<UpdateReviewQuestion> requests = List.of(
                new UpdateReviewQuestion(2L, "수정된 질문"),      // 수정
                new UpdateReviewQuestion(null, "새로운 질문")     // 생성
        );

        // when
        interviewQuestionManager.updateQuestions(requests, reviewId, review);

        // then
        // 삭제 검증
        assertThat(toDelete.isDeleted()).isTrue();

        // 수정 검증
        assertThat(toUpdate.getQuestion()).isEqualTo("수정된 질문");
        assertThat(toUpdate.isDeleted()).isFalse();

        // 생성 검증
        verify(interviewQuestionJpaRepository, times(1)).save(any(InterviewQuestion.class));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 수정 요청 시 예외가 발생한다")
    void 존재하지_않는_ID로_수정_요청시_예외가_발생한다() {
        // given
        var reviewId = 1L;

        given(interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE))
                .willReturn(List.of());

        List<UpdateReviewQuestion> requests = List.of(
                new UpdateReviewQuestion(999L, "존재하지 않는 질문")
        );

        // when & then
        assertThatThrownBy(() -> interviewQuestionManager.updateQuestions(requests, reviewId, review))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_INTERVIEW_QUESTION.getMessage());
    }

    @Test
    @DisplayName("빈 요청 목록이면 기존 질문이 모두 삭제된다")
    void 빈_요청_목록이면_기존_질문이_모두_삭제된다() {
        // given
        var reviewId = 1L;

        var existingQuestion1 = createQuestion(1L, "기존 질문1");
        var existingQuestion2 = createQuestion(2L, "기존 질문2");

        given(interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE))
                .willReturn(List.of(existingQuestion1, existingQuestion2));

        List<UpdateReviewQuestion> requests = List.of();

        // when
        interviewQuestionManager.updateQuestions(requests, reviewId, review);

        // then
        assertThat(existingQuestion1.isDeleted()).isTrue();
        assertThat(existingQuestion2.isDeleted()).isTrue();
    }

    private InterviewQuestion createQuestion(Long id, String question) {
        var interviewQuestion = InterviewQuestion.create(review, question);
        ReflectionTestUtils.setField(interviewQuestion, "id", id);
        return interviewQuestion;
    }

}