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
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewSummaryResponse;
import org.squad.careerhub.global.annotation.LoginMember;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

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
    public ResponseEntity<PageResponse<ReviewSummaryResponse>> findReviews(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "NEWEST") SortType sort,
            @RequestParam(required = false) Long lastReviewId,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        PageResponse<ReviewSummaryResponse> response = interviewReviewService.findReviews(
                query,
                sort,
                Cursor.of(lastReviewId, size)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> findReview(
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        ReviewDetailResponse response = interviewReviewService.findReview(reviewId, memberId);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<Void> update(
            @Valid @RequestBody ReviewUpdateRequest request,
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        interviewReviewService.update(
                request.toUpdateInterviewReview(),
                request.toUpdateInterviewQuestions(),
                reviewId,
                memberId
        );

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @LoginMember Long memberId
    ) {
        interviewReviewService.deleteReview(reviewId, memberId);

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