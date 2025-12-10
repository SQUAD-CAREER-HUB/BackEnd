package org.squad.careerhub.domain.community.interviewquestion.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Component
public class InterviewQuestionReader {

    private final InterviewQuestionJpaRepository interviewQuestionJpaRepository;

    public List<InterviewQuestion> findQuestionsByReview(Long reviewId) {
        return interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);
    }

}