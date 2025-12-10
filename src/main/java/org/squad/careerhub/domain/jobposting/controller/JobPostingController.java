package org.squad.careerhub.domain.jobposting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingExtractResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RestController
@RequestMapping("/v1/job-postings")
@RequiredArgsConstructor
public class JobPostingController extends JobPostingDocsController {

    @Override
    @GetMapping
    public ResponseEntity<JobPostingExtractResponse> getJobPosting(
        @RequestParam("url") String url,
        @LoginMember Long memberId

    ) {
        return ResponseEntity.ok(JobPostingExtractResponse.mock());
    }
}
