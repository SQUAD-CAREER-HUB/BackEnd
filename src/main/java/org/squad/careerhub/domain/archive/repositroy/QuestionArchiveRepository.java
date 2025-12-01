package org.squad.careerhub.domain.archive.repositroy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;

public interface QuestionArchiveRepository extends JpaRepository<QuestionArchive, Long> {

}