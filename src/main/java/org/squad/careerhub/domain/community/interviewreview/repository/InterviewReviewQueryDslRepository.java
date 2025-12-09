package org.squad.careerhub.domain.community.interviewreview.repository;

import static org.squad.careerhub.domain.community.interviewreview.entity.QInterviewReview.interviewReview;
import static org.squad.careerhub.domain.member.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Repository
public class InterviewReviewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<InterviewReview> findReviews(String query, SortType sort, Long lastReviewId, int limit) {
        return jpaQueryFactory.selectFrom(interviewReview)
                .join(interviewReview.author, member).fetchJoin()
                .where(
                        containsQuery(query),
                        getPaginationCondition(sort, lastReviewId),
                        isActive()
                )
                .orderBy(getOrderSpecifiers(sort))
                .limit(limit + 1)
                .fetch();
    }

    private BooleanExpression containsQuery(String query) {
        if (query == null) {
            return null;
        }

        return interviewReview.company.containsIgnoreCase(query)
                .or(interviewReview.position.containsIgnoreCase(query))
                .or(interviewReview.interviewType.containsIgnoreCase(query));
    }

    private BooleanExpression isActive() {
        return interviewReview.status.eq(EntityStatus.ACTIVE);
    }

    private BooleanExpression getPaginationCondition(SortType sort, Long lastReviewId) {
        if (lastReviewId == null) {
            return null;
        }

        return switch (sort) {
            case NEWEST -> interviewReview.id.lt(lastReviewId);
            case OLDEST -> interviewReview.id.gt(lastReviewId);
        };
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(SortType sort) {
        return switch (sort) {
            case NEWEST -> new OrderSpecifier[]{interviewReview.id.desc()};
            case OLDEST -> new OrderSpecifier[]{interviewReview.id.asc()};
        };
    }

}