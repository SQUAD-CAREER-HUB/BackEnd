package org.squad.careerhub.domain.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.repository.ApplicationQueryDslRepository;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Component
public class ApplicationReader {

    private final ApplicationQueryDslRepository applicationQueryDslRepository;

    public PageResponse<ApplicationSummaryResponse> findApplications(
            SearchCondition searchCondition,
            Cursor cursor,
            Long memberId
    ) {
        // Application 목록 조회 (limit + 1개)
        List<ApplicationSummaryResponse> applications = applicationQueryDslRepository.findApplications(searchCondition, cursor, memberId);

        boolean hasNext = hasNextPage(applications, cursor.limit());
        List<ApplicationSummaryResponse> currentPageApplications = getCurrentPageData(applications, cursor.limit());

        // 면접 일정 조회 및 병합
        List<ApplicationSummaryResponse> applicationsWithInterview = enrichWithUpcomingInterviews(currentPageApplications);

        // 다음 커서 계산
        Long nextCursorId = calculateNextCursor(applicationsWithInterview, hasNext);

        return new PageResponse<>(applicationsWithInterview, hasNext, nextCursorId);
    }

    private boolean hasNextPage(List<ApplicationSummaryResponse> applications, int limit) {
        return applications.size() > limit;
    }

     // 현재 페이지 데이터만 추출 (limit 개수만큼)
    private List<ApplicationSummaryResponse> getCurrentPageData(List<ApplicationSummaryResponse> applications, int limit) {
        return applications.size() > limit ? applications.subList(0, limit) : applications;
    }

     // 각 Application에 다가오는 면접 일정 정보 추가
    private List<ApplicationSummaryResponse> enrichWithUpcomingInterviews(List<ApplicationSummaryResponse> applications) {
        if (applications.isEmpty()) {
            return applications;
        }

        List<Long> applicationIds = applications.stream()
                .map(ApplicationSummaryResponse::applicationId)
                .toList();

        Map<Long, LocalDateTime> upcomingInterviews = applicationQueryDslRepository.findUpcomingInterviews(applicationIds);

        return applications.stream()
                .map(app -> app.withNextInterview(upcomingInterviews.get(app.applicationId())))
                .toList();
    }

    private Long calculateNextCursor(List<ApplicationSummaryResponse> applications, boolean hasNext) {
        if (!hasNext || applications.isEmpty()) {
            return null;
        }
        return applications.getLast().applicationId();
    }

}