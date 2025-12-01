package org.squad.careerhub.domain.schedule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.schedule.service.InterviewScheduleService;

@RequiredArgsConstructor
@RestController
public class InterviewScheduleController extends InterviewScheduleDocsController {

    private final InterviewScheduleService interviewScheduleService;

}