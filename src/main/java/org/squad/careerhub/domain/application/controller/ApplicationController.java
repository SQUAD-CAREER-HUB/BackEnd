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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.ApplicationUpdateRequest;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.service.ApplicationService;
import org.squad.careerhub.domain.application.service.dto.ApplicationDetailResponse;
import org.squad.careerhub.domain.application.service.dto.ApplicationPageResponse;
import org.squad.careerhub.domain.application.service.dto.ApplicationStatisticsResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RequiredArgsConstructor
@RestController
public class ApplicationController extends ApplicationDocsController {

    private final ApplicationService applicationService;

    @Override
    @PostMapping("/v1/applications")
    public ResponseEntity<Void> create(
            @Valid @RequestBody ApplicationCreateRequest request,
            @LoginMember Long memberId
    ) {
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
    public ResponseEntity<ApplicationPageResponse> getApplications(
            @RequestParam(required = false) String query,
            @RequestParam ApplicationStatus applicationStatus,
            @RequestParam(required = false) Long lastCursorId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(ApplicationPageResponse.mock());
    }

    @Override
    @GetMapping("/v1/applications/statistics")
    public ResponseEntity<ApplicationStatisticsResponse> getApplicationStatistics(@LoginMember Long memberId) {

        return ResponseEntity.ok(ApplicationStatisticsResponse.mock());
    }

    @Override
    @GetMapping("/v1/applications/in-progress")
    public ResponseEntity<ApplicationPageResponse> getInProgressApplications(@LoginMember Long memberId) {
        return ResponseEntity.ok(ApplicationPageResponse.inProgressMock());
    }

}