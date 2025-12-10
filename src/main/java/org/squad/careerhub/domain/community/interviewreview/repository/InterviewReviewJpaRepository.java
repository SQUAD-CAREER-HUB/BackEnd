package org.squad.careerhub.domain.community.interviewreview.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.global.entity.EntityStatus;

public interface InterviewReviewJpaRepository extends JpaRepository<InterviewReview, Long> {

    @Query("SELECT ir FROM InterviewReview ir JOIN FETCH ir.author WHERE ir.id = :reviewId AND ir.status = :status")
    Optional<InterviewReview> findByIdAndStatus(@Param("reviewId") Long reviewId, @Param("status") EntityStatus status);
}