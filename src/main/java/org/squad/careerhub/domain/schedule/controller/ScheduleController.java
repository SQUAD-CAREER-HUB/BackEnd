package org.squad.careerhub.domain.schedule.controller;


import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.service.ScheduleService;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RestController
@RequestMapping("/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController extends ScheduleDocsController {

    private final ScheduleService scheduleService;

    @Override
    @PostMapping("/interview")
    public ResponseEntity<ScheduleResponse> createInterviewFromCalendar(
        @Valid @RequestBody InterviewScheduleCreateRequest request,
        @LoginMember Long memberId
    ) {
        ScheduleResponse response = scheduleService.createInterviewFromCalendar(
            request.toApplicationInfo(),
            request.toNewInterviewSchedule(),
            memberId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/etc")
    public ResponseEntity<ScheduleResponse> createEtcFromCalendar(
        @Valid @RequestBody EtcScheduleCreateRequest request,
        @LoginMember Long memberId
    ) {
        ScheduleResponse response = scheduleService.createEtcFromCalendar(
            request.toApplicationInfo(),
            request.toNewEtcSchedule(),
            memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<ScheduleListResponse> getSchedule(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam
        String companyName,
        @RequestParam
        List<StageType> stageTypes,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(ScheduleListResponse.mock());
    }

}