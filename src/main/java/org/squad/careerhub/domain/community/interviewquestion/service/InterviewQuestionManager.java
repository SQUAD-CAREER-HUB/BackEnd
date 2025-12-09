package org.squad.careerhub.domain.community.interviewquestion.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;

@RequiredArgsConstructor
@Component
public class InterviewQuestionManager {

    private final InterviewQuestionJpaRepository interviewQuestionJpaRepository;

    public void createQuestions(List<String> interviewQuestions, InterviewReview review) {
        if (interviewQuestions == null) {
            return;
        }
        List<InterviewQuestion> questions = interviewQuestions.stream()
                .map(question -> InterviewQuestion.create(review, question))
                .toList();

        // NOTE: saveAll은 개수만큼 save 쿼리를 날림 MVP라 현재는 saveAll로 진행하지만 추후에 성능 이슈 있을 시 batch 처리 고려
        interviewQuestionJpaRepository.saveAll(questions);
    }

}