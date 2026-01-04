package org.squad.careerhub.domain.community.interviewreview.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        log.info("[Review] 면접 후기 생성 완료 - reviewId: {}, questionsCount: {}", review.getId(), interviewQuestions.size());

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

        log.info("[Review] 면접 후기 수정 완료 - reviewId: {}", reviewId);
    }

    public void deleteReview(Long reviewId, Long memberId) {
        interviewReviewManager.deleteReview(reviewId, memberId);

        log.info("[Review] 면접 후기 삭제 완료 - reviewId: {}", reviewId);
    }

    public PageResponse<ReviewSummaryResponse> findReviews(String query, SortType sort, Cursor cursor) {
        log.debug("[Review] 면접 후기 목록 조회 - query: {}, sort: {}", query, sort);

        return interviewReviewReader.findReviews(query, sort, cursor);
    }

    public ReviewDetailResponse findReview(Long reviewId, Long memberId) {
        log.debug("[Review] 면접 후기 상세 조회 - reviewId: {}, memberId: {}", reviewId, memberId);

        return interviewReviewReader.findReview(reviewId, memberId);
    }

}