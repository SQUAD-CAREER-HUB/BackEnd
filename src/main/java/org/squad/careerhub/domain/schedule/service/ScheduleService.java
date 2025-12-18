package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
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
    private final ApplicationJpaRepository applicationJpaRepository;

    public ScheduleResponse createInterviewFromCalendar(
        ApplicationInfo applicationInfo,
        NewInterviewSchedule newInterviewSchedule,
        Long memberId
    ) {
        Application app = getOwnedApplication(applicationInfo.applicationId(), memberId);

        Schedule saved = scheduleManager.createInterviewSchedule(app, newInterviewSchedule);
        return ScheduleResponse.from(saved);
    }

    public ScheduleResponse createEtcFromCalendar(
        ApplicationInfo applicationInfo,
        NewEtcSchedule newEtcSchedule,
        Long memberId
    ) {
        Application app = getOwnedApplication(applicationInfo.applicationId(), memberId);

        Schedule saved = scheduleManager.createEtcSchedule(app, newEtcSchedule);
        return ScheduleResponse.from(saved);
    }

    private Application getOwnedApplication(Long applicationId, Long memberId) {
        if (applicationId == null) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }
        Application app = applicationJpaRepository.findById(applicationId)
            .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));

        if (!app.getAuthor().getId().equals(memberId)) {
            throw new CareerHubException(ErrorStatus.FORBIDDEN_ERROR);
        }
        return app;
    }
}