package org.squad.careerhub.domain.archive.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.archive.entity.QuestionArchive;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;

@Schema(description = "지원서 질문 아카이브 응답 DTO")
@Builder
public record ApplicationQuestionArchiveResponse(
        @Schema(description = "지원서 질문 아카이브 ID", example = "1")
        Long questionArchiveId,

        @Schema(description = "인터뷰 유형", example = "기술 면접")
        String interviewType,

        @Schema(description = "질문 내용", example = "Spring IOC/DI에 대해 설명해 주세요.")
        String question
) {

    public static List<ApplicationQuestionArchiveResponse> from(List<QuestionArchive> archivedQuestionsByApplication) {
        return archivedQuestionsByApplication.stream()
                .map(questionArchive -> {
                    InterviewQuestion interviewQuestion = questionArchive.getInterviewQuestion();
                    return ApplicationQuestionArchiveResponse.builder()
                            .questionArchiveId(questionArchive.getId())
                            .interviewType(interviewQuestion.getInterviewReview().getInterviewType())
                            .question(interviewQuestion.getQuestion())
                            .build();
                })
                .toList();
    }

}