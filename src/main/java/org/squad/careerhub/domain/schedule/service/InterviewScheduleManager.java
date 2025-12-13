package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.schedule.repository.InterviewScheduleJpaRepository;

@RequiredArgsConstructor
@Component
public class InterviewScheduleManager {

    private final InterviewScheduleJpaRepository interviewScheduleJpaRepository;

    /**
     * 면접 전형일 경우 면접 일정 생성
     * TODO: 일정 담당자 구현 필요
     */
    public void createInterviewSchedules() {
        // TODO: 면접 일정 생성 로직 구현
    }

    /**
     * 기타 전형일 경우 기타 유형 일정 생성
     * TODO: 일정 담당자 구현 필요
     */
    public void createEtcSchedules() {
        // TODO: 기타 유형 일정 생성 로직 구현
    }

}