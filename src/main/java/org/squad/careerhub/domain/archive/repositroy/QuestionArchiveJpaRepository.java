package org.squad.careerhub.domain.archive.repositroy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;

public interface QuestionArchiveJpaRepository extends JpaRepository<QuestionArchive, Long> {

}