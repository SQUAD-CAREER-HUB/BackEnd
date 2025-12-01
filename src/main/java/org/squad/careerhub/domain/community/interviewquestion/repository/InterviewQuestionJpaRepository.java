package org.squad.careerhub.domain.community.interviewquestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;

public interface InterviewQuestionJpaRepository extends JpaRepository<InterviewQuestion, Long> {

}