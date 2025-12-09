package org.squad.careerhub.domain.community.interviewquestion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;

public interface InterviewQuestionJpaRepository extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByInterviewReviewId(Long reviewId);

}