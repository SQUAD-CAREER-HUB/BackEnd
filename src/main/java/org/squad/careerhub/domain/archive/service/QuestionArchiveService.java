package org.squad.careerhub.domain.archive.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionArchiveService {

    private final QuestionArchiveReader questionArchiveReader;

    public PageResponse<ApplicationQuestionArchiveResponse> findArchivedQuestionsByApplication(
            Long applicationId,
            Long memberId,
            Cursor cursor
    ) {
        log.debug("[Archive] 질문 보관함 조회 - applicationId: {}, memberId: {}", applicationId, memberId);

        return questionArchiveReader.findArchivedQuestionsByApplication(
                applicationId,
                memberId,
                cursor
        );
    }

}