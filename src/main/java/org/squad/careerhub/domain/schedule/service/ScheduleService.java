package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;

@RequiredArgsConstructor
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

        return ScheduleResponse.from(saved);
    }

}