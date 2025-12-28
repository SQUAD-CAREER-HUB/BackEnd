package org.squad.careerhub.domain.archive.repositroy;

import static org.squad.careerhub.domain.archive.entity.QQuestionArchive.questionArchive;
import static org.squad.careerhub.domain.community.interviewquestion.entity.QInterviewQuestion.interviewQuestion;
import static org.squad.careerhub.domain.community.interviewreview.entity.QInterviewReview.interviewReview;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.support.Cursor;

@RequiredArgsConstructor
@Repository
public class QuestionArchiveQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<QuestionArchive> findByApplicationId(Long applicationId, Long authorId, Cursor cursor) {
        return queryFactory
                .selectFrom(questionArchive)
                .join(questionArchive.interviewQuestion, interviewQuestion).fetchJoin()
                .join(interviewQuestion.interviewReview, interviewReview).fetchJoin()
                .where(
                        applicationEq(applicationId),
                        memberEq(authorId),
                        isActive(),
                        cursorCondition(cursor.lastCursorId())
                )
                .orderBy(questionArchive.id.desc())
                .limit(cursor.limit() + 1)
                .fetch();

    }

    private BooleanExpression cursorCondition(Long lastCursorId) {
        if (lastCursorId == null) {
            return null;
        }
        return questionArchive.id.lt(lastCursorId);
    }

    private BooleanExpression memberEq(Long authorId) {
        return interviewReview.author.id.eq(authorId);
    }

    private BooleanExpression applicationEq(Long applicationId) {
        return questionArchive.application.id.eq(applicationId);
    }

    private BooleanExpression isActive() {
        return questionArchive.status.eq(EntityStatus.ACTIVE);
    }

}