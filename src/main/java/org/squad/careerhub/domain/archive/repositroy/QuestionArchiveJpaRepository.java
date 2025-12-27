package org.squad.careerhub.domain.archive.repositroy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.global.entity.EntityStatus;

public interface QuestionArchiveJpaRepository extends JpaRepository<QuestionArchive, Long> {

    @Query("SELECT qa FROM QuestionArchive qa " +
            "JOIN FETCH qa.interviewQuestion iq JOIN FETCH iq.interviewReview ir " +
            "WHERE qa.application.id = :applicationId AND qa.status = :status"
    )
    List<QuestionArchive> findByApplicationIdAndStatus(
            @Param("applicationId") Long applicationId,
            @Param("status") EntityStatus status
    );

}