package org.squad.careerhub.domain.community.interviewreview.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.community.interviewquestion.service.InterviewQuestionManager;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;

@RequiredArgsConstructor
@Service
public class InterviewReviewService {

    private final InterviewReviewManager interviewReviewManager;
    private final InterviewQuestionManager interviewQuestionManager;

    @Transactional
    public Long createReview(NewInterviewReview newReview, List<String> interviewQuestions, Long authorId) {
        InterviewReview review = interviewReviewManager.createReview(newReview, authorId);

        interviewQuestionManager.createQuestions(interviewQuestions, review);

        return review.getId();
    }

}