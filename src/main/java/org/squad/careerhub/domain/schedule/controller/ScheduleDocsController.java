package org.squad.careerhub.domain.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Calendar API", description = "캘린더 API 문서입니다.")
public abstract class ScheduleDocsController {

    @Operation(
            summary = "면접 일정 등록(캘린더) - [JWT O]",
            description = """
                    ### 캘린더에서 면접 일정을 생성합니다.
                    - applicationId는 PathVariable로 전달합니다.
                    - datetime은 ISO8601(LocalDateTime) 포맷을 사용합니다.
                    """)
    @ApiResponse(
            responseCode = "201",
            description = "면접 일정 생성 성공",
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
    public abstract ResponseEntity<ScheduleResponse> createInterviewSchedule(
            @Parameter(
                    description = "지원서 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long applicationId,

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
            summary = "기타 일정 등록(캘린더) - [JWT O]",
            description = """
                    ### 캘린더에서 기타 일정을 생성합니다.
                    - applicationId는 PathVariable로 전달합니다.
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
    public abstract ResponseEntity<ScheduleResponse> createEtcSchedule(
            @Parameter(
                    description = "지원서 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long applicationId,

            @RequestBody(
                    description = "기타 일정 생성 요청 바디",
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
                    - from/to는 YYYY-MM-DD 형식이며 필수입니다.
                    - companyName으로 기업명을 필터링합니다. (현재 파라미터 필수)
                    - stageTypes로 전형 단계(서류/면접/기타 등)를 필터링합니다. (현재 파라미터 필수)
                    - submissionStatuses로 서류 상태(미제출/제출 등)를 필터링할 수 있습니다. (선택)
                    - resultCriteria(결과 기준)로 다음을 필터링할 수 있습니다. (선택)
                      - STAGE_PASS: 전형 합격(해당 '일정'이 합격인 것만)
                      - FINAL_PASS: 최종 합격(해당 지원서의 '모든 일정')
                      - FINAL_FAIL: 최종 불합격(해당 지원서의 '모든 일정')
                    - 결과는 startedAt 기준 오름차순 정렬로 반환됩니다.
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
                    example = "2026-01-01"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(
                    description = "조회 종료 날짜 (YYYY-MM-DD, 필수)",
                    required = true,
                    example = "2026-01-31"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @Parameter(
                    description = "기업명 필터 (현재 필수 파라미터)",
                    required = false,
                    example = "네이버"
            )
            @RequestParam
            String companyName,

            @Parameter(
                    description = "전형 단계 필터 (현재 필수 파라미터). 예: INTERVIEW,ETC,DOCUMENT",
                    required = false,
                    example = "INTERVIEW,ETC"
            )
            @RequestParam
            List<StageType> stageTypes,

            Long memberId,

            @Parameter(
                    description = "서류 상태 필터(선택). 예: NOT_SUBMITTED,SUBMITTED",
                    required = false,
                    example = "SUBMITTED"
            )
            @RequestParam(required = false)
            List<SubmissionStatus> submissionStatuses,

            @Parameter(
                    description = "결과 기준 필터(선택). STAGE_PASS | FINAL_PASS | FINAL_FAIL",
                    required = false,
                    example = "STAGE_PASS"
            )
            @RequestParam(required = false)
            ResultCriteria resultCriteria
    );

    @Operation(
            summary = "면접 일정 수정 - [JWT O]",
            description = """
                    ### 면접 일정을 수정합니다.
                    - applicationId / scheduleId는 PathVariable로 전달합니다.
                    - scheduleId는 해당 지원서(applicationId)의 면접 전형(StageType=INTERVIEW)에 속한 일정이어야 합니다.
                    - datetime은 ISO8601(LocalDateTime) 포맷을 사용합니다.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "면접 일정 수정 성공",
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
    public abstract ResponseEntity<ScheduleResponse> updateInterviewSchedule(
            @Parameter(description = "지원서 ID", required = true, example = "1")
            @PathVariable Long applicationId,

            @Parameter(description = "일정 ID", required = true, example = "100")
            @PathVariable Long scheduleId,

            @RequestBody(
                    description = "면접 일정 수정 요청 바디",
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
            summary = "기타 일정 수정 - [JWT O]",
            description = """
                    ### 기타 일정을 수정합니다.
                    - applicationId / scheduleId는 PathVariable로 전달합니다.
                    - scheduleId는 해당 지원서(applicationId)의 기타 전형(StageType=ETC)에 속한 일정이어야 합니다.
                    - datetime은 ISO8601(LocalDateTime) 포맷을 사용합니다.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "기타 일정 수정 성공",
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
    public abstract ResponseEntity<ScheduleResponse> updateEtcSchedule(
            @Parameter(description = "지원서 ID", required = true, example = "1")
            @PathVariable Long applicationId,

            @Parameter(description = "일정 ID", required = true, example = "100")
            @PathVariable Long scheduleId,

            @RequestBody(
                    description = "기타 일정 수정 요청 바디",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EtcScheduleUpdateRequest.class)
                    )
            )
            EtcScheduleUpdateRequest request,

            Long memberId
    );

    @Operation(
            summary = "일정 삭제(논리삭제) - [JWT O]",
            description = """
                    ### 일정을 논리삭제(status=DELETED) 처리합니다.
                    - applicationId / scheduleId는 PathVariable로 전달합니다.
                    - scheduleId는 해당 지원서(applicationId)에 속한 일정이어야 합니다.
                    - 삭제는 물리 삭제가 아니라 논리삭제이며, 조회 시 ACTIVE만 반환되도록 구성해야 합니다.
                    """
    )
    @ApiResponse(
            responseCode = "204",
            description = "일정 삭제 성공 (No Content)"
    )
    @ApiExceptions(values = {
            ErrorStatus.BAD_REQUEST,
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.NOT_FOUND,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> deleteSchedule(
            @Parameter(description = "지원서 ID", required = true, example = "1")
            @PathVariable Long applicationId,

            @Parameter(description = "일정 ID", required = true, example = "100")
            @PathVariable Long scheduleId,

            Long memberId
    );
}
