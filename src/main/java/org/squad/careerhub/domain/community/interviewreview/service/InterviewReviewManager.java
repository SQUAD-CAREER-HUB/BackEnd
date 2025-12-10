package org.squad.careerhub.domain.community.interviewreview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;

@RequiredArgsConstructor
@Component
public class InterviewReviewManager {

    private final MemberReader memberReader;
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

}