package org.squad.careerhub.domain.community.interviewreview.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewQueryDslRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewSummaryResponse;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Component
public class InterviewReviewReader {

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

}