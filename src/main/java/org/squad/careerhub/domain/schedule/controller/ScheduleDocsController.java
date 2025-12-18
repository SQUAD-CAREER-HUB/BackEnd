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
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.service.dto.InterviewScheduleResponse;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Calendar API", description = "캘린더 API 문서입니다.")
public abstract class ScheduleDocsController {

    @Operation(
        summary = "면접 일정 등록(캘린더) - [JWT O]",
        description = """
            ### 캘린더에서 면접 일정을 생성합니다.
            - applicationId는 RequestBody로 전달합니다.
            - datetime은 ISO8601(LocalDateTime) 포맷을 사용합니다.
            """)
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
    public abstract ResponseEntity<ScheduleResponse> createInterviewFromCalendar(
        @RequestBody(
            description = "면접 일정 생성 요청 바디 (applicationId 포함)",
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
        summary = "기타 일정 등록(캘린더) - [JWT O]",
        description = """
            ### 캘린더에서 기타 일정을 생성합니다.
            - applicationId는 RequestBody로 전달합니다.
            - datetime은 ISO8601(LocalDateTime) 포맷을 사용합니다.
            """
    )
    @ApiResponse(
        responseCode = "201",
        description = "기타 일정 생성 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ScheduleResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.NOT_FOUND,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ScheduleResponse> createEtcFromCalendar(
        @RequestBody(
            description = "기타 일정 생성 요청 바디 (applicationId 포함)",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EtcScheduleCreateRequest.class)
            )
        )
        EtcScheduleCreateRequest request,
        Long memberId
    );

    @Operation(
        summary = "캘린더 통합 일정 조회 - [JWT O]",
        description = """
            ### 캘린더에 표시할 일정을 통합 조회합니다.
            - 공고 마감(서류 전형 마감 단계), 면접 전형(모든 면접), 기타 전형(모든 기타 일정)을 합쳐서 반환합니다.
            - from/to는 YYYY-MM-DD 형식이며 필수입니다.
            - 기업이름으로 특정 기업만 필터링할 수 있습니다.
            - categories로 일정 분류(공고마감/면접/기타)를 필터링할 수 있습니다. (미전달 시 전체)
            - 결과는 datetime 오름차순으로 정렬됩니다.
            - 권장: from~to는 월 뷰 기준 31일 이내로 요청하세요.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "일정 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ScheduleListResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ScheduleListResponse> getSchedule(
        @Parameter(
            description = "조회 시작 날짜 (YYYY-MM-DD, 필수)",
            required = true,
            example = "2025-12-01"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @Parameter(
            description = "조회 종료 날짜 (YYYY-MM-DD, 필수)",
            required = true,
            example = "2025-12-31"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,

        @Parameter(
            description = "회사명",
            example = "구글코리아"
        )
        String companyName,

        @Parameter(
            description = "일정 분류 필터(선택, 미전달 시 전체)",
            example = "INTERVIEW,DOCUMENT"
        )
        List<StageType> stageTypes,


        Long memberId
    );
}
