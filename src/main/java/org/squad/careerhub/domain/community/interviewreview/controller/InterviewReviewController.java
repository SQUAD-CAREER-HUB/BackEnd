package org.squad.careerhub.domain.community.interviewreview.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewCreateRequest;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewReportRequest;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewUpdateRequest;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.service.InterviewReviewService;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewDetailResponse;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewPageResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RequiredArgsConstructor
@RestController
public class InterviewReviewController extends InterviewReviewDocsController {

    private final InterviewReviewService interviewReviewService;

    @Override
    @PostMapping("/v1/reviews")
    public ResponseEntity<Void> create(
            @Valid @RequestBody ReviewCreateRequest request,
            @LoginMember Long memberId
    ) {
        interviewReviewService.createReview(request.toNewInterviewReview(), request.interviewQuestions(), memberId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/v1/reviews")
    public ResponseEntity<ReviewPageResponse> getReviews(
            @RequestParam(required = false) String query,
            @RequestParam SortType sort,
            @RequestParam(required = false) Long lastReviewId,
            @RequestParam(required = false) Long lastLikeCount,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(ReviewPageResponse.mock());
    }

    @Override
    @GetMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> getReview(
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(ReviewDetailResponse.mock());
    }

    @Override
    @PatchMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<Void> update(
            @Valid @RequestBody ReviewUpdateRequest request,
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/v1/reviews/{reviewId}/report")
    public ResponseEntity<Void> report(
            @Valid @RequestBody ReviewReportRequest request,
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}