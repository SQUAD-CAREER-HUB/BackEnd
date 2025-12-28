package org.squad.careerhub.domain.archive.service;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.archive.repositroy.QuestionArchiveQueryDslRepository;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Component
public class QuestionArchiveReader {

    private final ApplicationReader applicationReader;
    private final QuestionArchiveQueryDslRepository questionArchiveQueryDslRepository;

    public PageResponse<ApplicationQuestionArchiveResponse> findArchivedQuestionsByApplication(Long applicationId, Long memberId, Cursor cursor) {
        if (!applicationReader.existByIdAndAuthorId(applicationId, memberId)) {
            throw new CareerHubException(ErrorStatus.NOT_FOUND_APPLICATION_BY_AUTHOR);
        }

        List<QuestionArchive> questionArchives = questionArchiveQueryDslRepository.findByApplicationId(
                applicationId,
                memberId,
                cursor
        );
        List<ApplicationQuestionArchiveResponse> responses = ApplicationQuestionArchiveResponse.from(questionArchives);

        boolean hasNext = hasNextPage(responses, cursor.limit());
        List<ApplicationQuestionArchiveResponse> currentPageQuestionArchives = getCurrentPageData(responses, cursor.limit());
        Long nextCursorId = calculateNextCursor(currentPageQuestionArchives, hasNext, ApplicationQuestionArchiveResponse::questionArchiveId);

        return new PageResponse<>(currentPageQuestionArchives, hasNext, nextCursorId);
    }

    private <T> boolean hasNextPage(List<T> questionArchives, int limit) {
        return questionArchives.size() > limit;
    }

    private <T> List<T> getCurrentPageData(List<T> applications, int limit) {
        return applications.size() > limit ? applications.subList(0, limit) : applications;
    }

    private <T> Long calculateNextCursor(
            List<T> items,
            boolean hasNext,
            Function<T, Long> idExtractor
    ) {
        if (!hasNext || items.isEmpty()) {
            return null;
        }
        return idExtractor.apply(items.getLast());
    }

}