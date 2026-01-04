package org.squad.careerhub.domain.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.repository.ScheduleQueryDslRepository;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleItem;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleListResponse;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class ScheduleReader {

    private final ScheduleJpaRepository scheduleJpaRepository;
    private final ScheduleQueryDslRepository scheduleQueryDslRepository;


    public List<Schedule> findSchedule(List<ApplicationStage> applicationStages, Long authorId) {
        return scheduleJpaRepository.findByApplicationStageInAndAuthorId(applicationStages,
                authorId);
    }

    public ScheduleListResponse getSchedule(
            LocalDate from,
            LocalDate to,
            String companyName,
            List<StageType> stageTypes,
            List<SubmissionStatus> submissionStatusList,
            ResultCriteria resultCriteria,
            Long memberId
    ) {
        LocalDateTime fromInclusive = from.atStartOfDay();
        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();

        List<ScheduleItem> items = scheduleQueryDslRepository.findCalendarItems(
                fromInclusive,
                toExclusive,
                companyName,
                stageTypes,
                submissionStatusList,
                resultCriteria,
                memberId
        );

        return ScheduleListResponse.from(items);
    }
}