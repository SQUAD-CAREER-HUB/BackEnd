package org.squad.careerhub.domain.community.interviewreview.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.service.MemberReader;

class InterviewReviewManagerUnitTest extends TestDoubleSupport {

    @Mock
    MemberReader memberReader;

    @Mock
    InterviewReviewJpaRepository interviewReviewJpaRepository;

    @InjectMocks
    InterviewReviewManager interviewReviewManager;

    @Test
    void 면접후기를_생성한다() {
        // given
        var member = Member.create("email@naver.com", SocialProvider.KAKAO, "socialId", "nickname", "profileImageUrl");
        given(memberReader.find(any())).willReturn(member);

        var interviewReview = InterviewReview.create(
                member,
                "카카오",
                "백엔드 개발자",
                "온라인 코딩 테스트",
                "코딩 테스트는 쉬웠고, 이후 면접은 어려웠습니다."
        );
        given(interviewReviewJpaRepository.save(any())).willReturn(interviewReview);

        // when
        var newReview = new NewInterviewReview(
                "카카오",
                "백엔드 개발자",
                "온라인 코딩 테스트",
                "코딩 테스트는 쉬웠고, 이후 면접은 어려웠습니다."
        );
        var review = interviewReviewManager.createReview(newReview, 1L);

        // then
        assertThat(review).isNotNull()
                .extracting(
                        InterviewReview::getCompany,
                        InterviewReview::getPosition,
                        InterviewReview::getInterviewType,
                        InterviewReview::getContent
                )
                .containsExactly(
                        newReview.company(),
                        newReview.position(),
                        newReview.interviewType(),
                        newReview.content()
                );
    }

}