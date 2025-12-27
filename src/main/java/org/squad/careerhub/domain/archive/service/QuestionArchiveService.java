package org.squad.careerhub.domain.archive.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;

@RequiredArgsConstructor
@Service
public class QuestionArchiveService {

    private final QuestionArchiveReader questionArchiveReader;

    public List<ApplicationQuestionArchiveResponse> findArchivedQuestionsByApplication(Long applicationId, Long memberId) {
        List<QuestionArchive> archivedQuestionsByApplication = questionArchiveReader.findArchivedQuestionsByApplication(
                applicationId,
                memberId
        );

        return ApplicationQuestionArchiveResponse.from(archivedQuestionsByApplication);
    }

}