package org.squad.careerhub.domain.schedule.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewSchedulePageResponse;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleResponse;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.service.InterviewScheduleService;
import org.squad.careerhub.global.annotation.LoginMember;

@RestController
@RequestMapping("/v1/interviews")
@RequiredArgsConstructor
public class InterviewScheduleController extends InterviewScheduleDocsController {

    @Override
    @PostMapping("/v1/applications/{applicationId}/interviews")
    public ResponseEntity<InterviewScheduleResponse> createInterview(
        @PathVariable Long applicationId,
        @Valid @RequestBody InterviewScheduleCreateRequest request,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.status(201).body(InterviewScheduleResponse.mock());
    }

    @Override
    @PatchMapping("/v1/interviews/{interviewId}")
    public ResponseEntity<InterviewScheduleResponse> updateInterview(
        @PathVariable Long interviewId,
        @Valid @RequestBody InterviewScheduleUpdateRequest request,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(InterviewScheduleResponse.mock());
    }

    @Override
    @DeleteMapping("/v1/interviews/{interviewId}")
    public ResponseEntity<Void> deleteInterview(
        @PathVariable Long interviewId,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/v1/interviews")
    public ResponseEntity<InterviewSchedulePageResponse> getInterviews(
        @RequestParam(required = false) Long applicationId,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) Long lastCursorId,
        @RequestParam(required = false, defaultValue = "20") Integer size,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(InterviewSchedulePageResponse.mock());
    }

    @Override
    @GetMapping("/v1/interviews/upcoming")
    public ResponseEntity<InterviewSchedulePageResponse> getUpcomingInterviews(
        @RequestParam(required = false, defaultValue = "7") Integer days,
        @RequestParam(required = false) Long lastCursorId,
        @RequestParam(required = false, defaultValue = "20") Integer size,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(InterviewSchedulePageResponse.mock());
    }
}