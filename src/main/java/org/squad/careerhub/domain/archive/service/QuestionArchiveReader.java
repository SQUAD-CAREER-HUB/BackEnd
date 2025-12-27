package org.squad.careerhub.domain.archive.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.archive.repositroy.QuestionArchiveJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class QuestionArchiveReader {

    private final ApplicationReader applicationReader;
    private final QuestionArchiveJpaRepository questionArchiveJpaRepository;

    public List<QuestionArchive> findArchivedQuestionsByApplication(Long applicationId, Long memberId) {
        if (!applicationReader.existByIdAndAuthorId(applicationId, memberId)) {
            throw new CareerHubException(ErrorStatus.NOT_FOUND_APPLICATION_BY_AUTHOR);
        }

        return questionArchiveJpaRepository.findByApplicationIdAndStatus(applicationId, EntityStatus.ACTIVE);
    }

}