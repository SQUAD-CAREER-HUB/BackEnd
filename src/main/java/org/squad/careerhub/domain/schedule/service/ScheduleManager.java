package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;

@RequiredArgsConstructor
@Component
public class ScheduleManager {

    private final ScheduleJpaRepository scheduleJpaRepository;

    /**
     * 면접 전형일 경우 면접 일정 생성
     * TODO: 일정 담당자 구현 필요
     */
    public void createInterviewSchedules() {
        // TODO: 면접 일정 생성 로직 구현, pass 된 서류 전형 일정도 함께 생성
    }

    /**
     * 기타 전형일 경우 기타 유형 일정 생성
     * TODO: 일정 담당자 구현 필요
     */
    public void createEtcSchedules() {
        // TODO: 기타 유형 일정 생성 로직 구현, pass 된 서류 전형 일정도 함께 생성
    }

    // NOTE:  서류 전형일 경우 서류 일정의 startedAt을 어떻게 할까요. endedAt은 application.deadline과 동일하게 하면 될 것 같은데..
    public void createDocumentSchedule() {

    }

}