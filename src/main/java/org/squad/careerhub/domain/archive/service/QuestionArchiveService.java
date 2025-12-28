package org.squad.careerhub.domain.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Service
public class QuestionArchiveService {

    private final QuestionArchiveReader questionArchiveReader;

    public PageResponse<ApplicationQuestionArchiveResponse> findArchivedQuestionsByApplication(
            Long applicationId,
            Long memberId,
            Cursor cursor
    ) {
        return questionArchiveReader.findArchivedQuestionsByApplication(
                applicationId,
                memberId,
                cursor
        );
    }

}