package org.squad.careerhub.domain.community.interviewreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;

public interface InterviewReviewJpaRepository extends JpaRepository<InterviewReview, Long> {

}