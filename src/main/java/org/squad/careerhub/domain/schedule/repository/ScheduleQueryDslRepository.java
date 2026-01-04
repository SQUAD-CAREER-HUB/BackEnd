package org.squad.careerhub.domain.schedule.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.QApplication;
import org.squad.careerhub.domain.application.entity.QApplicationStage;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.entity.QSchedule;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleItem;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Repository
public class ScheduleQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ScheduleItem> findCalendarItems(
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive,
            String company,
            List<StageType> stageTypes,
            List<SubmissionStatus> submissionStatusList,
            ResultCriteria resultCriteria,
            Long memberId
    ) {
        QSchedule s = QSchedule.schedule;
        QApplicationStage st = QApplicationStage.applicationStage;
        QApplication app = QApplication.application;

        BooleanBuilder where = new BooleanBuilder();

        // 1) 권한 + 논리삭제
        where.and(s.author.id.eq(memberId));
        where.and(s.status.eq(EntityStatus.ACTIVE));

        // 2) 날짜 범위(겹치는 일정 포함)
        where.and(
                s.startedAt.lt(toExclusive)
                        .and(s.endedAt.isNull().or(s.endedAt.goe(fromInclusive)))
        );

        // 3) 회사명 필터(선택)
        if (company != null && !company.isBlank()) {
            where.and(app.company.containsIgnoreCase(company));
        }

        // 4) 전형 단계 필터(선택)
        if (stageTypes != null && !stageTypes.isEmpty()) {
            where.and(st.stageType.in(stageTypes));
        }

        // 5) 서류 상태 필터(선택)
        if (submissionStatusList != null && !submissionStatusList.isEmpty()) {

            BooleanBuilder submissionWhere = new BooleanBuilder();

            // 1) DOCUMENT 일정: submissionStatus로 필터링
            submissionWhere.or(
                    st.stageType.eq(StageType.DOCUMENT)
                            .and(s.submissionStatus.in(submissionStatusList))
            );

            // 2) DOCUMENT가 아닌 일정: submissionStatus가 null인데, "SUBMITTED로 간주" 규칙 적용
            if (submissionStatusList.contains(SubmissionStatus.SUBMITTED)) {
                submissionWhere.or(
                        st.stageType.ne(StageType.DOCUMENT)
                                .and(s.submissionStatus.isNull())
                );
            }

            // NOT_SUBMITTED만 요청한 경우엔 위 or가 추가되지 않으므로
            // DOC 아닌 일정은 자연스럽게 제외됨.
            where.and(submissionWhere);
        }

        // 6) 결과 기준 필터(중요)
        if (resultCriteria != null) {
            switch (resultCriteria) {
                case STAGE_PASS -> {
                    // 전형 합격: "그 일정"만 합격인 것
                    where.and(s.scheduleResult.eq(ScheduleResult.PASS));
                }
                case FINAL_PASS -> {
                    // 최종 합격: 그 지원서의 모든 일정
                    where.and(app.applicationStatus.eq(ApplicationStatus.FINAL_PASS));
                }
                case FINAL_FAIL -> {
                    // 최종 불합격: 그 지원서의 모든 일정
                    where.and(app.applicationStatus.eq(ApplicationStatus.FINAL_FAIL));
                }
            }
        }

        return queryFactory
                .select(Projections.constructor(
                        ScheduleItem.class,
                        s.id,
                        app.id,
                        app.company,
                        st.stageType,
                        s.scheduleName,
                        s.startedAt,
                        s.endedAt,
                        s.location
                ))
                .from(s)
                .join(s.applicationStage, st)
                .join(st.application, app)
                .where(where)
                .orderBy(s.startedAt.asc(), s.id.asc())
                .fetch();
    }
}
