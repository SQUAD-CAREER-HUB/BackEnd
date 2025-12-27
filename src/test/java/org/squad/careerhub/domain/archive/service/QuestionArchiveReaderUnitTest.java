package org.squad.careerhub.domain.archive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.archive.repositroy.QuestionArchiveJpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
class QuestionArchiveReaderUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationReader applicationReader;

    @Mock
    QuestionArchiveJpaRepository questionArchiveJpaRepository;

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

        given(applicationReader.existByIdAndAuthorId(any(), any())).willReturn(true);
        given(questionArchiveJpaRepository.findByApplicationId(any())).willReturn(List.of(questionArchive));

        // when
        List<QuestionArchive> archivedQuestionsByApplication = questionArchiveReader.findArchivedQuestionsByApplication(1L, 1L);

        // then
        assertThat(archivedQuestionsByApplication).hasSize(1);
        assertThat(archivedQuestionsByApplication.getFirst().getApplication()).isNotNull();
        assertThat(archivedQuestionsByApplication.getFirst().getInterviewQuestion()).isNotNull();
        assertThat(archivedQuestionsByApplication.getFirst().getContent()).isNull();
    }

    @Test
    void 존재하지_않는_지원서로_질문_모음을_조회할_경우_예외가_발생한다() {
        // given
        given(applicationReader.existByIdAndAuthorId(any(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> questionArchiveReader.findArchivedQuestionsByApplication(1L, 1L))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_APPLICATION_BY_AUTHOR.getMessage());
    }

}
