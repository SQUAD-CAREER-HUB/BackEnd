package org.squad.careerhub.domain.community.interviewquestion.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;

class InterviewQuestionManagerUnitTest extends TestDoubleSupport {

    @Mock
    InterviewQuestionJpaRepository interviewQuestionJpaRepository;

    @InjectMocks
    InterviewQuestionManager interviewQuestionManager;

    @Test
    void 면접_질문을_저장한다() {
        // given
        List<String> interviewQuestions = List.of(
                "면접 질문 1",
                "면접 질문 2",
                "면접 질문 3"
        );
        var review = mock(InterviewReview.class);

        // when
        interviewQuestionManager.createQuestions(interviewQuestions, review);

        // then
        verify(interviewQuestionJpaRepository).saveAll(any());
    }

    @Test
    void 면접_질문이_null_이면_아무_일이_일어나지_않는다() {
        // given
        var review = mock(InterviewReview.class);

        // when
        interviewQuestionManager.createQuestions(null, review);

        // then
        verify(interviewQuestionJpaRepository, never()).saveAll(any());
    }

}