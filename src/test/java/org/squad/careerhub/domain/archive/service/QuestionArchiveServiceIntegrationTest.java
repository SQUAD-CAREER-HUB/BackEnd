package org.squad.careerhub.domain.archive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.archive.repositroy.QuestionArchiveJpaRepository;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewreview.service.InterviewReviewService;
import org.squad.careerhub.domain.community.interviewreview.service.dto.NewInterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@RequiredArgsConstructor
@Transactional
class QuestionArchiveServiceIntegrationTest extends IntegrationTestSupport {

    final InterviewReviewService interviewReviewService;
    final InterviewQuestionJpaRepository interviewQuestionJpaRepository;
    final MemberJpaRepository memberJpaRepository;
    final ApplicationJpaRepository applicationJpaRepository;
    final QuestionArchiveService questionArchiveService;
    final QuestionArchiveJpaRepository questionArchiveJpaRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(Member.create(
                "test@gmail.com",
                SocialProvider.KAKAO,
                "socialId",
                "TestUser",
                "profile.png"
        ));
    }

    @Test
    void 지원서에_저장된_질문_모음을_조회한다() {
        // given
        var application = applicationJpaRepository.save(Application.create(
                member,
                "jobPostingUrl",
                "companyName",
                "positionName",
                "jobLocation",
                StageType.INTERVIEW,
                ApplicationStatus.FINAL_PASS,
                ApplicationMethod.EMAIL,
                now().plusDays(2)
        ));
        var newReview = new NewInterviewReview(
                "company",
                "position",
                InterviewType.TECH.getDescription(),
                "content"
        );

        var interviewQuestions = List.of("question1", "question2", "question3");
        var reviewId = interviewReviewService.createReview(newReview, interviewQuestions, member.getId());
        interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(
                reviewId,
                org.squad.careerhub.global.entity.EntityStatus.ACTIVE
        ).forEach(question -> questionArchiveJpaRepository.save(QuestionArchive.create(application, question)));

        // when
        List<ApplicationQuestionArchiveResponse> responses = questionArchiveService.findArchivedQuestionsByApplication(
                application.getId(), member.getId()
        );

        // then
        assertThat(responses).hasSize(3).extracting(
                ApplicationQuestionArchiveResponse::interviewType,
                ApplicationQuestionArchiveResponse::question
        ).containsExactly(
                tuple(InterviewType.TECH.getDescription(), "question1"),
                tuple(InterviewType.TECH.getDescription(), "question2"),
                tuple(InterviewType.TECH.getDescription(), "question3")
        );
    }

    private LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

}