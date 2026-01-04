package org.squad.careerhub.domain.schedule.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.UpdateEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.UpdateInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class ScheduleService {

    private final ScheduleCreator scheduleCreator;
    private final ApplicationReader applicationReader;
    private final ScheduleUpdater scheduleUpdater;
    private final ScheduleReader scheduleReader;

    @Transactional
    public ScheduleResponse createInterviewSchedule(
            Long applicationId,
            NewInterviewSchedule newInterviewSchedule,
            Long memberId
    ) {
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);

        Schedule saved = scheduleCreator.createInterviewSchedule(app, newInterviewSchedule);

        log.info("[ScheduleService] 면접 일정 생성 완료 - scheduleId: {}", saved.getId());

        return ScheduleResponse.from(saved);
    }

    @Transactional
    public ScheduleResponse createEtcSchedule(
            Long applicationId,
            NewEtcSchedule newEtcSchedule,
            Long memberId
    ) {
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);

        Schedule saved = scheduleCreator.createEtcSchedule(app, newEtcSchedule);

        log.info("[ScheduleService] 기타 일정 생성 완료 - scheduleId: {}", saved.getId());

        return ScheduleResponse.from(saved);
    }

    @Transactional
    public ScheduleResponse updateInterviewSchedule(
            Long applicationId,
            Long scheduleId,
            UpdateInterviewSchedule dto,
            Long memberId
    ) {
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);

        Schedule updated = scheduleUpdater.updateInterviewSchedule(app, scheduleId, dto);

        log.info("[ScheduleService] 면접 일정 수정 완료 - scheduleId: {}", updated.getId());

        return ScheduleResponse.from(updated);
    }

    @Transactional
    public ScheduleResponse updateEtcSchedule(
            Long applicationId,
            Long scheduleId,
            UpdateEtcSchedule dto,
            Long memberId
    ) {
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);

        Schedule updated = scheduleUpdater.updateEtcSchedule(app, scheduleId, dto);

        log.info("[ScheduleService] 기타 일정 수정 완료 - scheduleId: {}", updated.getId());

        return ScheduleResponse.from(updated);
    }

    @Transactional
    public void deleteSchedule(Long applicationId, Long scheduleId, Long memberId) {
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);

        scheduleUpdater.deleteSchedule(app, scheduleId);

        log.info("[ScheduleService] 일정 삭제 완료 - scheduleId: {}", scheduleId);
    }

    @Transactional(readOnly = true)
    public ScheduleListResponse getSchedule(
            LocalDate from,
            LocalDate to,
            String companyName,
            List<StageType> stageTypes,
            List<SubmissionStatus> submissionStatusList,
            ResultCriteria resultCriteria,
            Long memberId
    ) {
        log.info("[ScheduleService] 일정 조회 완료");
        return scheduleReader.getSchedule(from, to, companyName, stageTypes, submissionStatusList,
                resultCriteria, memberId);
    }
}