package org.squad.careerhub.domain.application.service;

import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationQueryDslRepository;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationCreationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationDetailPageResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationInfoResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStageTimeLineListResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.service.ScheduleReader;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Component
public class ApplicationReader {

    private final ApplicationAttachmentReader applicationAttachmentReader;
    private final ApplicationStageReader applicationStageReader;
    private final ScheduleReader scheduleReader;
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

    @Transactional(readOnly = true)
    public ApplicationDetailPageResponse findApplication(Long applicationId, Long memberId) {
        Application application = applicationJpaRepository.findByIdAndAuthorId(applicationId, memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_APPLICATION));

        List<ApplicationAttachment> attachments = applicationAttachmentReader.findAttachments(applicationId);

        List<ApplicationStage> applicationStages = applicationStageReader.findApplicationStages(applicationId);
        List<Schedule> schedules = scheduleReader.findSchedule(applicationStages, memberId);

        return new ApplicationDetailPageResponse(
                 ApplicationInfoResponse.of(application, attachments),
                 ApplicationStageTimeLineListResponse.of(applicationStages, schedules)
         );
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
                now(),
                cursor
        );

        boolean hasNext = hasNextPage(responses, cursor.limit());
        List<BeforeDeadlineApplicationResponse> finalResponses = getCurrentPageData(responses, cursor.limit());
        Long nextCursorId = calculateNextCursor(finalResponses, hasNext, BeforeDeadlineApplicationResponse::applicationId);

        return new PageResponse<>(finalResponses, hasNext, nextCursorId);
    }

    public boolean existByIdAndAuthorId(Long applicationId, Long memberId) {
        return applicationJpaRepository.existsByIdAndAuthorId(applicationId, memberId);
    }
    /**
     * 주간/월간 생성된 지원서 통계 조회
     * @param memberId 회원 ID
     * @param weekCount 주간 통계 개수 (기본값 6주)
     * @param monthCount 월간 통계 개수 (기본값 6개월)
     */
    public ApplicationCreationStatisticsResponse getApplicationCreationStatistics(
            Long memberId,
            int weekCount,
            int monthCount
    ) {
        LocalDateTime now = now();

        // 주간 통계
        List<ApplicationCreationStatisticsResponse.WeeklyStatistics> weeklyStats = new ArrayList<>();
        for (int i = weekCount - 1; i >= 0; i--) {
            LocalDateTime weekStart = now.minusWeeks(i).with(java.time.DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            LocalDateTime weekEnd = weekStart.plusWeeks(1);

            long count = applicationJpaRepository.countByAuthorIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanAndStatus(
                    memberId,
                    weekStart,
                    weekEnd,
                    EntityStatus.ACTIVE
            );

            String period = String.format("%02d.%02d - %02d.%02d",
                    weekStart.getMonthValue(), weekStart.getDayOfMonth(),
                    weekEnd.minusDays(1).getMonthValue(), weekEnd.minusDays(1).getDayOfMonth());

            weeklyStats.add(ApplicationCreationStatisticsResponse.WeeklyStatistics.builder()
                    .period(period)
                    .count((int) count)
                    .isCurrentWeek(i == 0)
                    .build());
        }

        // 월간 통계
        List<ApplicationCreationStatisticsResponse.MonthlyStatistics> monthlyStats = new ArrayList<>();
        for (int i = monthCount - 1; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).toLocalDate().atStartOfDay();
            LocalDateTime monthEnd = monthStart.plusMonths(1);

            long count = applicationJpaRepository.countByAuthorIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanAndStatus(
                    memberId,
                    monthStart,
                    monthEnd,
                    EntityStatus.ACTIVE
            );

            String period = String.format("%d.%02d",
                    monthStart.getYear(), monthStart.getMonthValue());

            monthlyStats.add(ApplicationCreationStatisticsResponse.MonthlyStatistics.builder()
                    .period(period)
                    .count((int) count)
                    .isCurrentMonth(i == 0)
                    .build());
        }

        return ApplicationCreationStatisticsResponse.builder()
                .weeklyStatistics(weeklyStats)
                .monthlyStatistics(monthlyStats)
                .build();
    }

    private <T> boolean hasNextPage(List<T> data, int limit) {
        return data.size() > limit;
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