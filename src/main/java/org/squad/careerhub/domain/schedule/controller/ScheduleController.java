package org.squad.careerhub.domain.schedule.controller;


import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.service.ScheduleService;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;
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
    @GetMapping("/schedules")
    public ResponseEntity<ScheduleListResponse> getSchedule(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false)
            String companyName,
            @RequestParam(required = false)
            List<StageType> stageTypes,
            @LoginMember Long memberId,
            @RequestParam(required = false) List<SubmissionStatus> submissionStatuses,
            @RequestParam(required = false) ResultCriteria resultCriteria

    ) {
        return ResponseEntity.ok(
                scheduleService.getSchedule(from, to, companyName, stageTypes,
                        submissionStatuses, resultCriteria, memberId)
        );
    }

    @Override
    @PutMapping("/applications/{applicationId}/schedules/interview/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateInterviewSchedule(
            @PathVariable Long applicationId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody InterviewScheduleUpdateRequest request,
            @LoginMember Long memberId
    ) {
        ScheduleResponse response = scheduleService.updateInterviewSchedule(
                applicationId,
                scheduleId,
                request.toUpdateInterviewSchedule(),
                memberId
        );
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/applications/{applicationId}/schedules/etc/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateEtcSchedule(
            @PathVariable Long applicationId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody EtcScheduleUpdateRequest request,
            @LoginMember Long memberId
    ) {
        ScheduleResponse response = scheduleService.updateEtcSchedule(
                applicationId,
                scheduleId,
                request.toUpdateEtcSchedule(),
                memberId
        );
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/applications/{applicationId}/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long applicationId,
            @PathVariable Long scheduleId,
            @LoginMember Long memberId
    ) {
        scheduleService.deleteSchedule(
                applicationId,
                scheduleId,
                memberId
        );
        return ResponseEntity.noContent().build();
    }

}