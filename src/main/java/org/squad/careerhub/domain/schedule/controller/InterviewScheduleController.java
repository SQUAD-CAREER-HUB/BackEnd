package org.squad.careerhub.domain.schedule.controller;

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

@RestController
@RequestMapping("/v1/interviews")
@RequiredArgsConstructor
public class InterviewScheduleController extends InterviewScheduleDocsController {

    @Override
    @PostMapping
    public ResponseEntity<InterviewScheduleResponse> createInterview(
        @RequestBody InterviewScheduleCreateRequest request
    ) {
        return ResponseEntity.status(201).body(InterviewScheduleResponse.mock());
    }

    @Override
    @PatchMapping("/{interviewId}")
    public ResponseEntity<InterviewScheduleResponse> updateInterview(
        @PathVariable Long interviewId,
        @RequestBody InterviewScheduleUpdateRequest request
    ) {
        return ResponseEntity.ok(InterviewScheduleResponse.mock());
    }

    @Override
    @DeleteMapping("/{interviewId}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long interviewId) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<InterviewSchedulePageResponse> getInterviews(
        @RequestParam(required = false) Long applicationId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) Long lastCursorId,
        @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(InterviewSchedulePageResponse.mock());
    }

    @Override
    @GetMapping("/upcoming")
    public ResponseEntity<InterviewSchedulePageResponse> getUpcomingInterviews(
        @RequestParam(required = false, defaultValue = "7") Integer days,
        @RequestParam(required = false) Long lastCursorId,
        @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(InterviewSchedulePageResponse.mock());
    }
}