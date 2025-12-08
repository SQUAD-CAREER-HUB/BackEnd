package org.squad.careerhub.domain.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewSchedulePageResponse;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleResponse;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleUpdateRequest;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Interview Schedule API", description = "면접 일정 관리 API 문서입니다.")
public abstract class InterviewScheduleDocsController {

    @Operation(
        summary = "면접 일정 등록 - [JWT O]",
        description = """
                    ### 특정 지원 카드에 연동되는 면접 일정을 생성합니다.
                    - Endpoint: **POST /v1/applications/{applicationId}/interviews**
                    - 지원 카드 ID(applicationId)로 연동됩니다.
                    - 면접 유형(type)은 TECH/FIT/EXEC/TASK/TEST/OTHER 등을 사용할 수 있습니다.
                    - datetime은 ISO8601(LocalDateTime) 포맷을 사용합니다.
                    """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "201",
        description = "면접 일정 생성 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = InterviewScheduleResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<InterviewScheduleResponse> createInterview(
        @Parameter(
            description = "지원 카드 ID (PathVariable)",
            required = true,
            example = "1"
        )
        Long applicationId,
        @RequestBody(
            description = "면접 일정 생성 요청 바디",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InterviewScheduleCreateRequest.class)
            )
        )
        InterviewScheduleCreateRequest request,
        Long memberId
    );

    @Operation(
        summary = "면접 일정 수정 - [JWT O]",
        description = """
                    ### 기존 면접 일정을 부분 수정합니다.
                    - Endpoint: **PATCH /v1/interviews/{interviewId}**
                    - 전달된 필드만 수정됩니다. (PATCH)
                    - 면접 일정 변경 시 캘린더/알림과 동기화가 필요합니다. (서버 구현 시 고려)
                    """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "200",
        description = "면접 일정 수정 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = InterviewScheduleResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR,
        ErrorStatus.FORBIDDEN_MODIFY
    })
    public abstract ResponseEntity<InterviewScheduleResponse> updateInterview(
        @Parameter(description = "면접 ID", required = true, example = "10")
        Long interviewId,
        @RequestBody(
            description = "면접 일정 수정 요청 바디 (필드 선택적)",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InterviewScheduleUpdateRequest.class)
            )
        )
        InterviewScheduleUpdateRequest request,
        Long memberId
    );

    @Operation(
        summary = "면접 일정 삭제 - [JWT O]",
        description = """
                    ### 면접 일정을 삭제합니다.
                    - Endpoint: **DELETE /v1/interviews/{interviewId}**
                    - 연동된 캘린더 이벤트도 함께 삭제됩니다. (서버 구현 시 고려)
                    """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "204",
        description = "면접 일정 삭제 성공"
    )
    @ApiExceptions(values = {
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR,
        ErrorStatus.FORBIDDEN_DELETE
    })
    public abstract ResponseEntity<Void> deleteInterview(
        @Parameter(description = "면접 ID", required = true, example = "10")
        Long interviewId,
        Long memberId
    );

    @Operation(
        summary = "면접 일정 조회 (페이지네이션) - [JWT O]",
        description = """
                ### 조건에 따라 면접 일정을 커서 기반 페이지네이션으로 조회합니다.
                - Endpoint: **GET /v1/interviews**
                - applicationId(지원 카드 ID)로 특정 카드의 면접만 조회할 수 있습니다.
                - from/to(YYYY-MM-DD)로 날짜 범위를 필터링할 수 있습니다.
                - lastCursorId가 없으면 첫 페이지를 조회합니다.
                - lastCursorId에는 이전 응답의 nextCursorId를 그대로 넘기면 됩니다.
                - size는 한 페이지에 가져올 면접 일정 개수이며, 기본값은 20입니다.
                """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "200",
        description = "면접 일정 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = InterviewSchedulePageResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<InterviewSchedulePageResponse> getInterviews(
        @Parameter(
            description = "지원 카드 ID (선택)",
            example = "1"
        )
        Long applicationId,

        @Parameter(
            description = "조회 시작 날짜 (YYYY-MM-DD, 선택)",
            example = "2025-12-01"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @Parameter(
            description = "조회 종료 날짜 (YYYY-MM-DD, 선택)",
            example = "2025-12-31"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,

        @Parameter(
            description = "커서 기반 페이지네이션을 위한 마지막 면접 ID (선택)",
            example = "15"
        )
        Long lastCursorId,

        @Parameter(
            description = "페이지 크기 (기본값 20)",
            example = "20"
        )
        Integer size,
        Long memberId
    );

    @Operation(
        summary = "다가오는 면접 일정 조회 (페이지네이션) - [JWT O]",
        description = """
                ### N일 이내에 예정된 면접 일정을 조회합니다.
                - Endpoint: **GET /v1/interviews/upcoming**
                - days 파라미터를 지정하지 않으면 기본 7일 이내 면접을 조회합니다.
                - 결과는 커서 기반 페이지네이션 형식으로 반환됩니다.
                """,
        security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
        responseCode = "200",
        description = "다가오는 면접 일정 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = InterviewSchedulePageResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<InterviewSchedulePageResponse> getUpcomingInterviews(
        @Parameter(
            description = "며칠 이내의 면접을 조회할지 (기본값 7)",
            example = "7"
        )
        Integer days,

        @Parameter(
            description = "커서 기반 페이지네이션을 위한 마지막 면접 ID (선택)",
            example = "15"
        )
        Long lastCursorId,

        @Parameter(
            description = "페이지 크기 (기본값 20)",
            example = "20"
        )
        Integer size,
        Long memberId
    );
}