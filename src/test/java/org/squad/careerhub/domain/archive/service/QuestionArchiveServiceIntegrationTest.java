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
import org.squad.careerhub.domain.application.ApplicationFixture;
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
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Transactional
class QuestionArchiveServiceIntegrationTest extends IntegrationTestSupport {

    final InterviewReviewService interviewReviewService;
    final InterviewQuestionJpaRepository interviewQuestionJpaRepository;
    final MemberJpaRepository memberJpaRepository;
    final ApplicationJpaRepository applicationJpaRepository;
    final QuestionArchiveService questionArchiveService;
    final QuestionArchiveJpaRepository questionArchiveJpaRepository;

    Member author;

    @BeforeEach
    void setUp() {
        author = memberJpaRepository.save(MemberFixture.createMember());
    }

    @Test
    void 지원서에_저장된_질문_모음을_조회한다() {
        // given
        var application = applicationJpaRepository.save(ApplicationFixture.createApplicationDocs(author));
        var newReview = new NewInterviewReview(
                "company",
                "position",
                InterviewType.TECH.getDescription(),
                "content"
        );

        var interviewQuestions = List.of("question1", "question2", "question3");
        var reviewId = interviewReviewService.createReview(newReview, interviewQuestions, author.getId());
        interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(
                reviewId,
                EntityStatus.ACTIVE
        ).forEach(question -> questionArchiveJpaRepository.save(QuestionArchive.create(application, question)));

        // when
        PageResponse<ApplicationQuestionArchiveResponse> response = questionArchiveService.findArchivedQuestionsByApplication(
                application.getId(), author.getId(),
                Cursor.of(null, 10));

        // then
        assertThat(response).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );
        assertThat(response.contents()).hasSize(3).extracting(
                ApplicationQuestionArchiveResponse::interviewType,
                ApplicationQuestionArchiveResponse::question
        ).containsExactly(
                tuple(InterviewType.TECH.getDescription(), "question3"),
                tuple(InterviewType.TECH.getDescription(), "question2"),
                tuple(InterviewType.TECH.getDescription(), "question1")
        );
    }

    @Test
    void 커서_페이지네이션_작동을_검증한다() {
        // given
        var application = applicationJpaRepository.save(ApplicationFixture.createApplicationDocs(author));
        var newReview = new NewInterviewReview(
                "company",
                "position",
                InterviewType.TECH.getDescription(),
                "content"
        );
        var interviewQuestions = List.of(
                "question1", "question2", "question3", "question4", "question5",
                "question6", "question7", "question8", "question9", "question10",
                "question11", "question12", "question13", "question14", "question15",
                "question16", "question17", "question18", "question19", "question20",
                "question21", "question22", "question23", "question24", "question25"
        );
        var reviewId = interviewReviewService.createReview(newReview, interviewQuestions, author.getId());
        interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(
                reviewId,
                EntityStatus.ACTIVE
        ).forEach(question -> questionArchiveJpaRepository.save(QuestionArchive.create(application, question)));

        // when
        PageResponse<ApplicationQuestionArchiveResponse> firstPage = questionArchiveService.findArchivedQuestionsByApplication(
                application.getId(), author.getId(),
                Cursor.of(null, 10));

        PageResponse<ApplicationQuestionArchiveResponse> secondPage = questionArchiveService.findArchivedQuestionsByApplication(
                application.getId(), author.getId(),
                Cursor.of(firstPage.nextCursorId(), 10));

        PageResponse<ApplicationQuestionArchiveResponse> thirdPage = questionArchiveService.findArchivedQuestionsByApplication(
                application.getId(), author.getId(),
                Cursor.of(secondPage.nextCursorId(), 10));

        // then

        // 1번째 페이지
        assertThat(firstPage).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                true,
                firstPage.contents().getLast().questionArchiveId()
        );

        assertThat(firstPage.contents()).hasSize(10).extracting(
                ApplicationQuestionArchiveResponse::question
        ).containsExactly(
                "question25", "question24", "question23", "question22", "question21",
                "question20", "question19", "question18", "question17", "question16"
        );

        // 2번째 페이지
        assertThat(secondPage).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                true,
                secondPage.contents().getLast().questionArchiveId()
        );

        assertThat(secondPage.contents()).hasSize(10).extracting(
                ApplicationQuestionArchiveResponse::question
        ).containsExactly(
                "question15", "question14", "question13", "question12", "question11",
                "question10", "question9", "question8", "question7", "question6"
        );

        // 3번째 페이지
        assertThat(thirdPage).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );

        assertThat(thirdPage.contents()).hasSize(5).extracting(
                ApplicationQuestionArchiveResponse::question
        ).containsExactly(
                "question5", "question4", "question3", "question2", "question1"
        );

    }

}