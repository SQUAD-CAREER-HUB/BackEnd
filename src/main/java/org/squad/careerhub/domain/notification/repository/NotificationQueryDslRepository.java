package org.squad.careerhub.domain.notification.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.squad.careerhub.domain.notification.entity.Notification;
import org.squad.careerhub.domain.notification.entity.QNotification;
import org.squad.careerhub.global.entity.EntityStatus;

@Repository
@RequiredArgsConstructor
public class NotificationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<Notification> findMyNotifications(Long memberId, Long cursorId, int size) {
        QNotification n = QNotification.notification;

        BooleanExpression base = n.memberId.eq(memberId)
                .and(n.status.eq(EntityStatus.ACTIVE));

        BooleanExpression cursorCond = (cursorId == null) ? null : n.id.lt(cursorId);

        return queryFactory
                .selectFrom(n)
                .where(base, cursorCond)
                .orderBy(n.id.desc())
                .limit(size + 1L)
                .fetch();
    }
}
