package org.squad.careerhub.domain.application.repository;

import static com.querydsl.core.types.Projections.constructor;
import static org.squad.careerhub.domain.application.entity.QApplication.application;
import static org.squad.careerhub.domain.application.entity.QApplicationStage.applicationStage;
import static org.squad.careerhub.domain.schedule.entity.QSchedule.schedule;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageResult;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.support.Cursor;

@RequiredArgsConstructor
@Repository
public class ApplicationQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ApplicationSummaryResponse> findApplications(
            SearchCondition searchCondition,
            Cursor cursor,
            Long memberId
    ) {
        return jpaQueryFactory.select(constructor(ApplicationSummaryResponse.class,
                        application.id.as("applicationId"),
                        application.company,
                        application.position,
                        application.currentStageType,
                        applicationStage.stageStatus.as("currentStageStatus"),
                        application.applicationStatus,
                        application.deadline,
                        application.applicationMethod,
                        schedule.stageName,
                        schedule.location,
                        schedule.startedAt
                ))
                .from(application)
                .leftJoin(applicationStage).on(applicationStage.application.id.eq(application.id)
                        .and(applicationStage.stageType.eq(application.currentStageType))
                ).leftJoin(schedule).on(schedule.application.id.eq(application.id)
                        .and(schedule.stageType.eq(application.currentStageType))
                        // 가장 빠른 일정만
                        .and(schedule.startedAt.eq(
                                JPAExpressions
                                        .select(schedule.startedAt.min())
                                        .from(schedule)
                                        .where(schedule.application.id.eq(application.id)
                                                .and(schedule.stageType.eq(application.currentStageType)))
                        )))
                .where(
                        memberEq(memberId),
                        searchByKeyword(searchCondition.query()),
                        searchByStageTypes(searchCondition.stageTypes()),
                        searchBySubmissionStatus(searchCondition.stageTypes(), searchCondition.submissionStatus()),
                        searchByStageResult(searchCondition.stageResult()),
                        paginationCondition(cursor.lastCursorId()),
                        isActive()
                )
                .orderBy(application.id.desc())
                .limit(cursor.limit() + 1)
                .fetch();
    }

    public List<BeforeDeadlineApplicationResponse> findBeforeDeadLineFromApplication(
            Long authorId,
            StageType stageType,
            LocalDate today,
            Cursor cursor
    ) {
        return jpaQueryFactory.select(Projections.constructor(BeforeDeadlineApplicationResponse.class,
                                application.id.as("applicationId"),
                                application.company,
                                application.position,
                                application.submittedAt,
                                application.deadline,
                                applicationStage.submissionStatus
                        )
                ).from(application)
                .join(applicationStage).on(applicationStage.application.id.eq(application.id)
                        .and(applicationStage.stageType.eq(application.currentStageType))
                )
                .where(
                        cursorCondition(cursor.lastCursorId()),
                        memberEq(authorId),
                        stageTypeEq(stageType),
                        deadlineGoe(today),
                        isActive()
                )
                .orderBy(application.id.desc())
                .limit(cursor.limit() + 1)
                .fetch();
    }

    private BooleanExpression cursorCondition(Long lastCursorId) {
        if (lastCursorId == null) {
            return null;
        }
        return application.id.lt(lastCursorId);
    }

    private BooleanExpression deadlineGoe(LocalDate today) {
        today = today == null ? LocalDate.now() : today;

        return application.deadline.goe(today);
    }

    private BooleanExpression stageTypeEq(StageType stageType) {
        if (stageType == null) {
            return null;
        }
        return application.currentStageType.eq(stageType);
    }

    private BooleanExpression memberEq(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return application.author.id.eq(authorId);
    }

    private BooleanExpression searchByKeyword(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        String trimmedQuery = query.trim();

        return application.company.containsIgnoreCase(trimmedQuery)
                .or(application.position.containsIgnoreCase(trimmedQuery));
    }

    private BooleanExpression searchByStageTypes(List<StageType> stageTypes) {
        if (stageTypes == null || stageTypes.isEmpty()) {
            return null;
        }
        return application.currentStageType.in(stageTypes);
    }

    // DOCUMENT 전형일 경우에만 필터링 가능
    private BooleanExpression searchBySubmissionStatus(List<StageType> stageTypes, List<SubmissionStatus> submissionStatus) {
        if (submissionStatus == null || !stageTypes.contains(StageType.DOCUMENT)) {
            return null;
        }
        return applicationStage.submissionStatus.in(submissionStatus);
    }

    private BooleanExpression searchByStageResult(List<StageResult> stageResults) {
        if (stageResults == null || stageResults.isEmpty()) {
            return null;
        }

        return stageResults.stream()
                .map(stageResult -> switch (stageResult) {
                    case STAGE_PASS -> applicationStage.stageStatus.eq(StageStatus.PASS);
                    case FINAL_PASS -> application.applicationStatus.eq(ApplicationStatus.FINAL_PASS);
                    case FINAL_FAIL -> application.applicationStatus.eq(ApplicationStatus.FINAL_FAIL);
                })
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

    private BooleanExpression paginationCondition(Long lastCursorId) {
        if (lastCursorId == null) {
            return null;
        }
        return application.id.lt(lastCursorId);
    }

    private BooleanExpression isActive() {
        return application.status.eq(EntityStatus.ACTIVE);
    }

}