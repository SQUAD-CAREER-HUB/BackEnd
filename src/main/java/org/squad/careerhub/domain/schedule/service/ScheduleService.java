package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class ScheduleService {

    private final ScheduleCreator scheduleCreator;
    private final ApplicationReader applicationReader;

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

}

