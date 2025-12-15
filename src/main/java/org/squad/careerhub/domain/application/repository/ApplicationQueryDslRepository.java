package org.squad.careerhub.domain.application.repository;

import static com.querydsl.core.types.Projections.constructor;
import static org.squad.careerhub.domain.application.entity.QApplication.application;
import static org.squad.careerhub.domain.application.entity.QApplicationStage.applicationStage;
import static org.squad.careerhub.domain.schedule.entity.QInterviewSchedule.interviewSchedule;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.squad.careerhub.domain.application.entity.StageResult;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.support.Cursor;

@RequiredArgsConstructor
@Repository
public class ApplicationQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ApplicationSummaryResponse> findApplications(SearchCondition searchCondition, Cursor cursor, Long memberId) {
        return jpaQueryFactory.select(constructor(ApplicationSummaryResponse.class,
                        application.id,
                        application.company,
                        application.position,
                        application.currentStageType,
                        applicationStage.stageStatus.stringValue(),
                        application.submittedAt,
                        application.deadline,
                        Expressions.nullExpression(LocalDateTime.class)
                ))
                .from(application)
                .leftJoin(applicationStage).on(applicationStage.application.id.eq(application.id))
                .where(
                        memberEq(memberId),
                        searchByKeyword(searchCondition.query()),
                        searchByStageTypes(searchCondition.stageTypes()),
                        searchBySubmissionStatus(searchCondition.stageTypes(), searchCondition.submissionStatus()),
                        searchByStageResult(searchCondition.stageResult()),
                        paginationCondition(cursor.lastCursorId()),
                        isActive()
                )
                .groupBy(application.id)
                .orderBy(application.id.desc())
                .limit(cursor.limit() + 1)
                .fetch();
    }

    // 가장 가까운 면접 일정 조회
    public Map<Long, LocalDateTime> findUpcomingInterviews(List<Long> applicationIds) {
        LocalDateTime now = LocalDateTime.now();

        List<Tuple> results = jpaQueryFactory
                .select(
                        interviewSchedule.application.id,
                        interviewSchedule.scheduledAt.min()
                )
                .from(interviewSchedule)
                .where(
                        interviewSchedule.application.id.in(applicationIds),
                        interviewSchedule.scheduledAt.goe(now)
                )
                .groupBy(interviewSchedule.application.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(interviewSchedule.application.id),
                        tuple -> tuple.get(interviewSchedule.scheduledAt.min())
                ));
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
    private BooleanExpression searchBySubmissionStatus(List<StageType> stageTypes, SubmissionStatus submissionStatus) {
        if (submissionStatus == null || !stageTypes.contains(StageType.DOCUMENT)) {
            return null;
        }
        return applicationStage.submissionStatus.eq(submissionStatus);
    }

    private BooleanExpression searchByStageResult(StageResult stageResult) {
        if (stageResult == null) {
            return null;
        } else if (stageResult == StageResult.STAGE_PASS) {
            return applicationStage.stageStatus.eq(StageStatus.PASS);
        } else if (stageResult == StageResult.FINAL_PASS) {
            return applicationStage.stageType.eq(StageType.FINAL_PASS);
        } else {
            return applicationStage.stageType.eq(StageType.FINAL_FAIL);
        }
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