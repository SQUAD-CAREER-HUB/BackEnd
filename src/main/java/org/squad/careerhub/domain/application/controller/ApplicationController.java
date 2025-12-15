package org.squad.careerhub.domain.application.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.ApplicationUpdateRequest;
import org.squad.careerhub.domain.application.entity.StageResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.ApplicationService;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationDetailResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.global.annotation.LoginMember;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@RestController
public class ApplicationController extends ApplicationDocsController {

    private final ApplicationService applicationService;

    @Override
    @PostMapping("/v1/applications")
    public ResponseEntity<Void> create(
            @Valid @RequestPart ApplicationCreateRequest request,
            @RequestPart(required = false) List<MultipartFile> files,
            @LoginMember Long memberId
    ) {
        applicationService.createApplication(
                request.toNewJobPosting(),
                request.toNewApplicationInfo(),
                request.toNewStage(),
                files,
                memberId
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PatchMapping("/v1/applications/{applicationId}")
    public ResponseEntity<Void> update(
            @Valid @RequestPart ApplicationUpdateRequest request,
            @RequestPart(required = false) List<MultipartFile> files,
            @PathVariable Long applicationId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/v1/applications/{applicationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long applicationId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/v1/applications/{applicationId}")
    public ResponseEntity<ApplicationDetailResponse> getApplication(
            @PathVariable Long applicationId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(ApplicationDetailResponse.mock());
    }

    @Override
    @GetMapping("/v1/applications")
    public ResponseEntity<PageResponse<ApplicationSummaryResponse>> findApplications(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<StageType> stageType,
            @RequestParam(required = false) List<SubmissionStatus> submissionStatus,
            @RequestParam(required = false) List<StageResult> stageResult,
            @RequestParam(required = false) Long lastCursorId,
            @RequestParam(required = false, defaultValue = "20") int size,
            @LoginMember Long memberId
    ) {
        PageResponse<ApplicationSummaryResponse> response = applicationService.findApplications(
                new SearchCondition(
                        query,
                        stageType,
                        submissionStatus,
                        stageResult
                ),
                Cursor.of(lastCursorId, size),
                memberId
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/v1/applications/statistics")
    public ResponseEntity<ApplicationStatisticsResponse> getApplicationStatistics(@LoginMember Long memberId) {

        return ResponseEntity.ok(ApplicationStatisticsResponse.mock());
    }

    @Override
    @GetMapping("/v1/applications/in-progress")
    public ResponseEntity<PageResponse<ApplicationSummaryResponse>> getInProgressApplications(@LoginMember Long memberId) {
        // TODO: 진행 중인 지원서 목록 조회 구현 필요
        return ResponseEntity.ok(PageResponse.mock());
    }

}