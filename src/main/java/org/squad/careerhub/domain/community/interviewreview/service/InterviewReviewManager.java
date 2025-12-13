package org.squad.careerhub.domain.community.interviewreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.community.interviewquestion.service.InterviewQuestionManager;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.community.interviewreview.service.dto.UpdateInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class InterviewReviewManager {

    private final MemberReader memberReader;
    private final InterviewQuestionManager interviewQuestionManager;
    private final InterviewReviewJpaRepository interviewReviewJpaRepository;

    public InterviewReview createReview(NewInterviewReview newReview, Long authorId) {
        Member author = memberReader.find(authorId);

        return interviewReviewJpaRepository.save(InterviewReview.create(
                author,
                newReview.company(),
                newReview.position(),
                newReview.interviewType(),
                newReview.content()
        ));
    }

    public InterviewReview updateReview(UpdateInterviewReview updateReview, Long reviewId, Long memberId) {
        InterviewReview interviewReview = interviewReviewJpaRepository.findByIdAndStatus(reviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_REVIEW));

        if (!interviewReview.isAuthor(memberId)) {
            throw new CareerHubException(ErrorStatus.FORBIDDEN_MODIFY);
        }

        interviewReview.updateReview(
                updateReview.company(),
                updateReview.position(),
                updateReview.interviewType(),
                updateReview.content()
        );

        return interviewReview;
    }

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        InterviewReview interviewReview = interviewReviewJpaRepository.findByIdAndStatus(reviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_REVIEW));

        if (!interviewReview.isAuthor(memberId)) {
            throw new CareerHubException(ErrorStatus.FORBIDDEN_DELETE);
        }

        interviewQuestionManager.deleteQuestionsByReview(reviewId);

        interviewReview.delete();
    }

}