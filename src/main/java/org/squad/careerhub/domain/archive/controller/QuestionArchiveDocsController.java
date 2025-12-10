package org.squad.careerhub.domain.archive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionCreateRequest;
import org.squad.careerhub.domain.archive.service.dto.PersonalQuestionPageResponse;
import org.squad.careerhub.domain.archive.service.dto.PersonalQuestionResponse;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionUpdateRequest;
import org.squad.careerhub.global.annotation.LoginMember;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;


@Tag(
    name = "Personal Interview Question API",
    description = "개인 면접 질문(내 질문 리스트) 관리 API 문서입니다."
)
public abstract class QuestionArchiveDocsController {

    @Operation(
        summary = "(질문 모음) 개인 면접 질문 등록",
        description = """
                질문 모음(아카이브)에 개인 면접 질문을 등록합니다.
                applicationId를 함께 전달하면 해당 지원 카드와 연결된 질문으로 저장됩니다.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "개인 면접 질문 등록 성공",
            content = @Content(schema = @Schema(implementation = PersonalQuestionResponse.class))
        )
    })
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PersonalQuestionResponse> registerQuestion(
        @Parameter(
            description = "질문을 연결할 지원 카드 ID (선택 값)",
            example = "1"
        )
        Long applicationId,
        @RequestBody(
            description = "개인 면접 질문 등록 요청 본문",
            required = true,
            content = @Content(schema = @Schema(implementation = PersonalQuestionCreateRequest.class))
        )
        PersonalQuestionCreateRequest request,
        @Parameter(hidden = true) @LoginMember Long memberId
    );

    @Operation(
        summary = "(질문 모음) 개인 면접 질문 조회 (페이징)",
        description = """
                질문 모음(아카이브)에 저장된 개인 면접 질문 목록을 조회합니다.
                lastCursorId 기준 커서 기반 페이징을 제공합니다.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "개인 면접 질문 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PersonalQuestionPageResponse.class))
        )
    })
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PersonalQuestionPageResponse> getQuestions(
        @Parameter(
            description = "마지막으로 조회한 질문 ID (커서)",
            example = "30"
        )
        Long lastCursorId,
        @Parameter(
            description = "한 번에 조회할 데이터 개수",
            example = "20"
        )
        Integer size,
        @Parameter(hidden = true) @LoginMember Long memberId
    );

    @Operation(
        summary = "(질문 모음)개인 면접 질문 아카이브 삭제",
        description = "질문 모음(아카이브)에 저장된 개인 면접 질문 하나를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "개인 면접 질문 삭제 성공"
        )
    })
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> deleteQuestion(
        @Parameter(
            description = "삭제할 질문 ID",
            example = "10",
            required = true
        )
        Long questionId,
        @Parameter(hidden = true) @LoginMember Long memberId
    );

    @Operation(
        summary = "(질문 모음) 개인 면접 질문 아카이브 수정",
        description = "질문 모음(아카이브)에 저장된 개인 면접 질문 내용을 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "개인 면접 질문 수정 성공",
            content = @Content(schema = @Schema(implementation = PersonalQuestionResponse.class))
        )
    })
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PersonalQuestionResponse> updateQuestion(
        @Parameter(
            description = "수정할 질문 ID",
            example = "10",
            required = true
        )
        Long questionId,
        @RequestBody(
            description = "개인 면접 질문 수정 요청 본문",
            required = true,
            content = @Content(schema = @Schema(implementation = PersonalQuestionUpdateRequest.class))
        )
        PersonalQuestionUpdateRequest request,
        @Parameter(hidden = true) @LoginMember Long memberId
    );

    @Operation(
        summary = "지원서 내 개인 면접 질문으로 등록 - [JWT O]",
        description = """
                    ### 개인 면접 질문을 등록합니다.
                    - 대상: 특정 지원 카드(applicationId)에 연결된 '내 인터뷰 질문'입니다.
                    - 사용 시나리오:
                      - 커뮤니티 면접 질문을 내 질문 리스트로 가져오기 (interviewQuestionId 사용)
                      - 내가 직접 작성한 질문/답변을 저장하기 (question + answer 사용)
                    - 제약:
                      - `interviewQuestionId`와 `question`은 둘 중 하나만 필수로 사용하도록 서비스 레벨에서 검증 가능
                    """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "201",
        description = "개인 면접 질문 등록 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PersonalQuestionResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PersonalQuestionResponse> registerPersonalQuestion(
        @Parameter(
            description = "지원 카드 ID",
            required = true,
            example = "1"
        )
        Long applicationId,

        @RequestBody(
            description = "개인 면접 질문 등록 요청 바디",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PersonalQuestionCreateRequest.class)
            )
        )
        PersonalQuestionCreateRequest request,
        Long memberId
    );

    @Operation(
        summary = "지원서 내 개인 면접 질문 목록 조회 (페이지네이션) - [JWT O]",
        description = """
                    ### 특정 지원 카드에 연결된 개인 면접 질문 목록을 조회합니다. (커서 기반 페이지네이션)
                    - Query:
                      - lastCursorId: 마지막으로 조회한 질문 ID (optional)
                      - size: 페이지 크기 (optional, default = 20)
                    - 응답:
                      - questions: 질문/답변 목록
                      - hasNext: 다음 페이지 존재 여부
                      - nextCursorId: 다음 페이지 조회 시 사용할 커서 ID
                    """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "200",
        description = "개인 면접 질문 목록 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PersonalQuestionPageResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PersonalQuestionPageResponse> getPersonalQuestions(
        @Parameter(
            description = "지원 카드 ID",
            required = true,
            example = "1"
        )
        Long applicationId,

        @Parameter(
            description = "마지막으로 조회한 질문 ID (커서 기반 페이지네이션)",
            example = "25",
            required = false
        )
        Long lastCursorId,

        @Parameter(
            description = "페이지 크기 (기본값 20)",
            example = "20",
            required = false
        )
        Integer size,
        Long memberId
    );

    @Operation(
        summary = "지원서 내 개인 면접 질문 제거 - [JWT O]",
        description = """
                    ### 내 개인 면접 질문 리스트에서 질문을 제거합니다.
                    - 커뮤니티 면접 질문(InterviewQuestion)은 삭제되지 않습니다.
                    - PersonalInterviewQuestion(Personal용 링크)만 soft delete 또는 제거 처리됩니다.
                    """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "204",
        description = "개인 면접 질문 제거 성공"
    )
    @ApiExceptions(values = {
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.FORBIDDEN_DELETE,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> deletePersonalQuestion(
        @Parameter(
            description = "지원 카드 ID",
            required = true,
            example = "1"
        )
        Long applicationId,

        @Parameter(
            description = "개인 면접 질문 ID",
            required = true,
            example = "10"
        )
        Long questionId,
        Long memberId
    );

    @Operation(
        summary = "지원서 내 개인 면접 질문 수정 - [JWT O]",
        description = """
                    ### 내 개인 면접 질문을 수정합니다.
                    - 대상: 특정 지원 카드(applicationId)에 연결된 '내 인터뷰 질문' 중 하나입니다.
                    - 사용 시나리오:
                      - 기존에 저장해둔 질문 내용을 수정하고 싶을 때
                      - 답변 내용을 보완/수정하고 싶을 때
                    - 제약:
                      - 본인이 작성한 질문만 수정 가능합니다. (권한 없으면 403)
                      - question, answer 둘 중 일부만 수정해도 되고, 둘 다 수정해도 됩니다.
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "200",
        description = "개인 면접 질문 수정 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PersonalQuestionResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.FORBIDDEN_MODIFY,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PersonalQuestionResponse> updatePersonalQuestion(
        @Parameter(
            description = "지원 카드 ID",
            required = true,
            example = "1"
        )
        Long applicationId,

        @Parameter(
            description = "개인 면접 질문 ID",
            required = true,
            example = "10"
        )
        Long questionId,

        @RequestBody(
            description = "개인 면접 질문 수정 요청 바디",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PersonalQuestionUpdateRequest.class)
            )
        )
        PersonalQuestionUpdateRequest request,
        Long memberId
    );
}