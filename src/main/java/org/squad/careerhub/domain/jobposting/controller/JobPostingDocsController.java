package org.squad.careerhub.domain.jobposting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.jobposting.controller.dto.JobPostingExtractResponse;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Job Posting API", description = "채용 공고 URL 기반 정보 추출 API 문서입니다.")
public abstract class JobPostingDocsController {

    @Operation(
        summary = "채용 공고 정보 조회 (AI 추출) - [JWT O]",
        description = """
                    ### 채용 공고 URL을 기반으로 회사/직무/JD/마감일/전형 정보를 자동 추출합니다.
                    - 로그인한 사용자만 사용할 수 있습니다.
                    - 지원자가 채용 공고 URL을 입력하면 서버에서 Gemini 등의 LLM을 통해 정보를 추출합니다.
                    - AI 추출 실패 또는 일부만 추출된 경우, status 필드로 상태를 반환하며
                      프론트에서 결과 확인 화면을 띄운 뒤 수동 입력을 유도할 수 있습니다.
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "200",
        description = "채용 공고 정보 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = JobPostingExtractResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<JobPostingExtractResponse> getJobPosting(
        @Parameter(
            name = "url",
            description = "채용 공고 URL (원티드, 사람인, 잡코리아, 랠릿 등)",
            required = true,
            example = "https://www.wanted.co.kr/wd/323219"
        )
        String url
    );
}