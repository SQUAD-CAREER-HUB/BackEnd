package org.squad.careerhub.domain.community.interviewreview.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.service.InterviewQuestionReader;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.InterviewQuestionResponse;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewQueryDslRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewDetailResponse;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewSummaryResponse;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Component
public class InterviewReviewReader {

    private final InterviewQuestionReader interviewQuestionReader;
    private final InterviewReviewJpaRepository interviewReviewJpaRepository;
    private final InterviewReviewQueryDslRepository interviewReviewQueryDslRepository;

    public PageResponse<ReviewSummaryResponse> findReviews(String query, SortType sort, Cursor cursor) {
        List<InterviewReview> reviews = interviewReviewQueryDslRepository.findReviews(
                query,
                sort,
                cursor.lastCursorId(),
                cursor.limit()
        );

        boolean hasNext = reviews.size() > cursor.limit();
        List<InterviewReview> finalReviews = hasNext ? reviews.subList(0, cursor.limit()) : reviews;
        List<ReviewSummaryResponse> responses = finalReviews.stream()
                .map(ReviewSummaryResponse::from)
                .toList();
        Long nextCursorId = hasNext && !finalReviews.isEmpty() ? finalReviews.getLast().getId() : null;

        return new PageResponse<>(responses, hasNext, nextCursorId);
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse findReview(Long reviewId, Long memberId) {
        InterviewReview interviewReview = interviewReviewJpaRepository.findByIdAndStatus(reviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_REVIEW));

        List<InterviewQuestion> questionsByReview = interviewQuestionReader.findQuestionsByReview(reviewId);
        List<InterviewQuestionResponse> questionResponses = InterviewQuestionResponse.from(questionsByReview);

        return ReviewDetailResponse.of(interviewReview, questionResponses, memberId);
    }

}