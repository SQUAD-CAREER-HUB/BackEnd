package org.squad.careerhub.domain.community.interviewreview.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.community.interviewquestion.service.InterviewQuestionManager;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.UpdateReviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.community.interviewreview.service.dto.UpdateInterviewReview;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewDetailResponse;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewSummaryResponse;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Service
public class InterviewReviewService {

    private final InterviewReviewManager interviewReviewManager;
    private final InterviewReviewReader interviewReviewReader;
    private final InterviewQuestionManager interviewQuestionManager;

    @Transactional
    public Long createReview(NewInterviewReview newReview, List<String> interviewQuestions, Long authorId) {
        InterviewReview review = interviewReviewManager.createReview(newReview, authorId);

        interviewQuestionManager.createQuestions(interviewQuestions, review);

        return review.getId();
    }

    @Transactional
    public void update(
            UpdateInterviewReview updateInterviewReview,
            List<UpdateReviewQuestion> updateReviewQuestions,
            Long reviewId,
            Long memberId
    ) {
        InterviewReview review = interviewReviewManager.updateReview(updateInterviewReview, reviewId, memberId);

        interviewQuestionManager.updateQuestions(updateReviewQuestions, reviewId, review);
    }

    public void deleteReview(Long reviewId, Long memberId) {
        interviewReviewManager.deleteReview(reviewId, memberId);
    }


    public PageResponse<ReviewSummaryResponse> findReviews(String query, SortType sort, Cursor cursor) {
        return interviewReviewReader.findReviews(query, sort, cursor);
    }

    public ReviewDetailResponse findReview(Long reviewId, Long memberId) {
        return interviewReviewReader.findReview(reviewId, memberId);
    }

}