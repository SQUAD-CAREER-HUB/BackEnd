package org.squad.careerhub.domain.community.interviewquestion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.global.entity.EntityStatus;

public interface InterviewQuestionJpaRepository extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByInterviewReviewIdAndStatus(Long reviewId, EntityStatus status);

}