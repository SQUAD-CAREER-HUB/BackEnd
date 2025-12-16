package org.squad.careerhub.domain.application.repository;

import static org.squad.careerhub.domain.application.entity.QApplication.application;
import static org.squad.careerhub.domain.application.entity.QApplicationStage.applicationStage;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.support.Cursor;

@RequiredArgsConstructor
@Repository
public class ApplicationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<BeforeDeadlineApplicationResponse> findBeforeDeadLineFromApplication(
            Long authorId,
            StageType stageType,
            LocalDate today,
            Cursor cursor
    ) {
        return queryFactory.select(Projections.constructor(BeforeDeadlineApplicationResponse.class,
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

    private BooleanExpression isActive() {
        return application.status.eq(EntityStatus.ACTIVE);
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

}