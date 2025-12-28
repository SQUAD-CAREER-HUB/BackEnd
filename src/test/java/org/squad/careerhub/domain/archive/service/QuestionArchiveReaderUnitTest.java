package org.squad.careerhub.domain.archive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.archive.repositroy.QuestionArchiveQueryDslRepository;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
class QuestionArchiveReaderUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationReader applicationReader;

    @Mock
    QuestionArchiveQueryDslRepository questionArchiveQueryDslRepository;

    @InjectMocks
    QuestionArchiveReader questionArchiveReader;

    @Test
    void 지원서별_저장된_질문_모음을_조회한다() {
        // given
        var member = mock(Member.class);
        var application1 = mock(Application.class);
        var interviewReview = InterviewReview.create(
                member,
                "company",
                "position",
                InterviewType.TECH.getDescription(),
                "content"
        );
        var interviewQuestion = InterviewQuestion.create(interviewReview, "questionContent");
        var questionArchive = QuestionArchive.create(application1, interviewQuestion);
        ReflectionTestUtils.setField(questionArchive, "id", 1L);
        var cursor = Cursor.of(null, 10);
        given(applicationReader.existByIdAndAuthorId(any(), any())).willReturn(true);
        given(questionArchiveQueryDslRepository.findByApplicationId(any(), any(), any())).willReturn(List.of(questionArchive));

        // when
        PageResponse<ApplicationQuestionArchiveResponse> response = questionArchiveReader.findArchivedQuestionsByApplication(
                1L,
                1L,
                cursor
        );

        // then
        assertThat(response).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );
        assertThat(response.contents()).hasSize(1).extracting(
                ApplicationQuestionArchiveResponse::questionArchiveId,
                ApplicationQuestionArchiveResponse::question,
                ApplicationQuestionArchiveResponse::interviewType
        ).containsExactly(
                tuple(1L, "questionContent", InterviewType.TECH.getDescription())
        );
    }

    @Test
    void 존재하지_않는_지원서로_질문_모음을_조회할_경우_예외가_발생한다() {
        // given
        given(applicationReader.existByIdAndAuthorId(any(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> questionArchiveReader.findArchivedQuestionsByApplication(1L, 1L, Cursor.of(null, 10)))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_APPLICATION_BY_AUTHOR.getMessage());
    }

}
