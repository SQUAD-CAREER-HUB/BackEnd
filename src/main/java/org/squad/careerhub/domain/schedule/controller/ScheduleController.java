package org.squad.careerhub.domain.schedule.controller;


import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.service.ScheduleService;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ScheduleController extends ScheduleDocsController {

    private final ScheduleService scheduleService;

    @Override
    @PostMapping("/applications/{applicationId}/schedules/interview")
    public ResponseEntity<ScheduleResponse> createInterviewSchedule(
            @PathVariable Long applicationId,
            @Valid @RequestBody InterviewScheduleCreateRequest request,
            @LoginMember Long memberId
    ) {
        ScheduleResponse response = scheduleService.createInterviewSchedule(
                applicationId,
                request.toNewInterviewSchedule(),
                memberId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/applications/{applicationId}/schedules/etc")
    public ResponseEntity<ScheduleResponse> createEtcSchedule(
            @PathVariable Long applicationId,
            @Valid @RequestBody EtcScheduleCreateRequest request,
            @LoginMember Long memberId
    ) {
        ScheduleResponse response = scheduleService.createEtcSchedule(
                applicationId,
                request.toNewEtcSchedule(),
                memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/schedule")
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