package org.squad.careerhub.domain.community.interviewreview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.community.interviewreview.repository.InterviewReviewJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.community.interviewreview.service.dto.UpdateInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.service.MemberReader;
import org.squad.careerhub.global.error.CareerHubException;

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

    @Test
    void 면접후기를_수정한다() {
        // given
        var member = Member.create("email@naver.com", SocialProvider.KAKAO, "socialId", "nickname", "profileImageUrl");
        ReflectionTestUtils.setField(member, "id", 1L);
        var interviewReview = InterviewReview.create(member, "카카오", "백엔드", "온라인", "내용");

        given(interviewReviewJpaRepository.findByIdAndStatus(any(), any()))
                .willReturn(Optional.of(interviewReview));

        var updateReview = new UpdateInterviewReview("네이버", "프론트엔드", "대면", "수정된 내용");

        // when
        var result = interviewReviewManager.updateReview(updateReview, 1L, member.getId());

        // then
        assertThat(result).extracting(
                InterviewReview::getCompany,
                InterviewReview::getPosition,
                InterviewReview::getInterviewType,
                InterviewReview::getContent
        ).containsExactly(
                updateReview.company(),
                updateReview.position(),
                updateReview.interviewType(),
                updateReview.content()
        );
    }

    @Test
    void 작성자가_아니면_수정_실패() {
        // given
        var member = Member.create("email@naver.com", SocialProvider.KAKAO, "socialId", "nickname", "profileImageUrl");
        ReflectionTestUtils.setField(member, "id", 999999L);
        var interviewReview = InterviewReview.create(member, "카카오", "백엔드", "온라인", "내용");

        given(interviewReviewJpaRepository.findByIdAndStatus(any(), any()))
                .willReturn(Optional.of(interviewReview));

        // when & then
        assertThatThrownBy(() -> interviewReviewManager.updateReview(new UpdateInterviewReview("company", "position", "1차면접","content"), 1L, 999L))
            .isInstanceOf(CareerHubException.class);
    }

}