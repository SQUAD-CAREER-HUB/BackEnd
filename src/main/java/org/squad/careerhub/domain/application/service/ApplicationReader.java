package org.squad.careerhub.domain.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationQueryDslRepository;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Component
public class ApplicationReader {

    private final ApplicationJpaRepository applicationJpaRepository;
    private final ApplicationQueryDslRepository applicationQueryDslRepository;

    public Application findApplication(Long applicationId) {
        return applicationJpaRepository.findById(applicationId)
            .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));
    }

    public PageResponse<ApplicationSummaryResponse> findApplications(
            SearchCondition searchCondition,
            Cursor cursor,
            Long memberId
    ) {
        // Application 목록 조회 (limit + 1개)
        List<ApplicationSummaryResponse> applications = applicationQueryDslRepository.findApplications(
                searchCondition,
                cursor,
                memberId
        );

        boolean hasNext = hasNextPage(applications, cursor.limit());
        List<ApplicationSummaryResponse> currentPageApplications = getCurrentPageData(applications, cursor.limit());

        // 다음 커서 계산
        Long nextCursorId = calculateNextCursor(currentPageApplications, hasNext, ApplicationSummaryResponse::applicationId);

        return new PageResponse<>(currentPageApplications, hasNext, nextCursorId);
    }

    // NOTE: 한방 쿼리로 처리해야 될 지 고민해보기 (트래픽이 많은 API임. 성능 중요)
    @Transactional(readOnly = true)
    public ApplicationStatisticsResponse getApplicationStatistics(Long authorId) {
        int totalApplications = applicationJpaRepository.countByAuthorIdAndStatus(authorId, EntityStatus.ACTIVE);
        int docsStageCount = applicationJpaRepository.countByAuthorIdAndCurrentStageTypeAndStatus(
                authorId,
                StageType.DOCUMENT,
                EntityStatus.ACTIVE
        );
        int interviewStageCount = applicationJpaRepository.countByAuthorIdAndCurrentStageTypeAndStatus(
                authorId,
                StageType.INTERVIEW,
                EntityStatus.ACTIVE
        );
        int etcStageCount = applicationJpaRepository.countByAuthorIdAndCurrentStageTypeAndStatus(
                authorId,
                StageType.ETC,
                EntityStatus.ACTIVE
        );
        int finalPassCount = applicationJpaRepository.countByAuthorIdAndApplicationStatusAndStatus(
                authorId,
                ApplicationStatus.FINAL_PASS,
                EntityStatus.ACTIVE
        );
        int finalFailCount = applicationJpaRepository.countByAuthorIdAndApplicationStatusAndStatus(
                authorId,
                ApplicationStatus.FINAL_FAIL,
                EntityStatus.ACTIVE
        );

        return ApplicationStatisticsResponse.of(
                totalApplications,
                docsStageCount,
                interviewStageCount,
                etcStageCount,
                finalPassCount,
                finalFailCount
        );
    }

    public PageResponse<BeforeDeadlineApplicationResponse> findBeforeDeadlineApplications(Long authorId, Cursor cursor) {
        List<BeforeDeadlineApplicationResponse> responses = applicationQueryDslRepository.findBeforeDeadLineFromApplication(
                authorId,
                StageType.DOCUMENT,
                LocalDate.now(),
                cursor
        );

        boolean hasNext = hasNextPage(responses, cursor.limit());
        List<BeforeDeadlineApplicationResponse> finalResponses = getCurrentPageData(responses, cursor.limit());
        Long nextCursorId = calculateNextCursor(finalResponses, hasNext, BeforeDeadlineApplicationResponse::applicationId);

        return new PageResponse<>(finalResponses, hasNext, nextCursorId);
    }

    private <T> boolean hasNextPage(List<T> applications, int limit) {
        return applications.size() > limit;
    }

    // 현재 페이지 데이터만 추출 (limit 개수만큼)
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