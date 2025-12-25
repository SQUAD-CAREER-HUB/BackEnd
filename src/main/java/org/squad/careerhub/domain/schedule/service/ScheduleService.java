package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.service.dto.ApplicationInfo;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleManager scheduleManager;
    private final ApplicationReader applicationReader;

    public ScheduleResponse createInterviewFromCalendar(
        ApplicationInfo applicationInfo,
        NewInterviewSchedule newInterviewSchedule,
        Long memberId
    ) {
        Long applicationId = applicationInfo.applicationId();
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);
        Schedule saved = scheduleManager.createInterviewSchedule(app, newInterviewSchedule);
        return ScheduleResponse.from(saved);
    }

    public ScheduleResponse createEtcFromCalendar(
        ApplicationInfo applicationInfo,
        NewEtcSchedule newEtcSchedule,
        Long memberId
    ) {
        Long applicationId = applicationInfo.applicationId();
        Application app = applicationReader.findApplication(applicationId);
        app.validateOwnedBy(memberId);
        Schedule saved = scheduleManager.createEtcSchedule(app, newEtcSchedule);
        return ScheduleResponse.from(saved);
    }


}