package org.squad.careerhub.domain.application.controller;

import static org.squad.careerhub.global.error.ErrorStatus.BAD_REQUEST;
import static org.squad.careerhub.global.error.ErrorStatus.FORBIDDEN_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.INTERNAL_SERVER_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.ApplicationUpdateRequest;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.service.dto.ApplicationDetailResponse;
import org.squad.careerhub.domain.application.service.dto.ApplicationPageResponse;
import org.squad.careerhub.domain.application.service.dto.ApplicationStatisticsResponse;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Application", description = "지원서 관련 API 문서")
public abstract class ApplicationDocsController {

    @Operation(
            summary = "지원 카드 등록 - JWT O",
            description = "새로운 지원 카드를 등록합니다."
    )
    @RequestBody(
            description = "지원 카드 등록 요청",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationCreateRequest.class)
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "지원 카드 등록 성공"
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> create(
            ApplicationCreateRequest request,
            Long memberId
    );

    @Operation(
            summary = "지원 카드 수정 - JWT O",
            description = """
                    #### 기존에 등록된 지원 카드의 정보를 수정합니다.<br><br>
                    - **[요청 형식]** <br>
                      - **Content-Type**: multipart/form-data<br>
                      - JSON 데이터와 파일을 함께 전송합니다.<br><br>
                    
                    - **[요청 파라미터]**<br>
                      - **request** : 지원 카드 수정 데이터 (JSON 형식, Part name: "request")<br>
                      - **files**: 첨부 파일 목록 (선택 사항, 최대 5개, Part name: "files")<br><br>
                    
                    - **[제약 사항]** <br>
                      - 본인이 등록한 지원서만 수정할 수 있습니다.<br>
                    """
    )
    @ApiResponse(
            responseCode = "204",
            description = "지원 카드 수정 성공"
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> update(
            ApplicationUpdateRequest request,
            @Parameter(
                    description = "첨부 파일 목록 (선택 사항, 최대 5개)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            List<MultipartFile> files,
            @Parameter(
                    description = "지원 카드 ID",
                    example = "1",
                    required = true
            )
            Long applicationId,
            Long memberId
    );

    @Operation(
            summary = "지원 카드 삭제 - JWT O",
            description = "기존에 등록된 지원 카드를 삭제합니다. 본인이 등록한 지원서만 삭제할 수 있습니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "지원 카드 삭제 성공"
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(
                    description = "지원 카드 ID",
                    example = "1",
                    required = true
            )
            Long applicationId,
            Long memberId

    );

    @Operation(
            summary = "지원 카드 상세 조회 - JWT O",
            description = """
                    ## 본인이 등록한 지원 카드의 상세 정보를 조회합니다.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "지원 카드 상세 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationDetailResponse.class)
            )
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            FORBIDDEN_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationDetailResponse> getApplication(
            @Parameter(
                    description = "지원 카드 ID",
                    example = "1",
                    required = true
            )
            Long applicationId,
            Long memberId
    );


    @Operation(
            summary = "지원 카드 목록 조회 - JWT O",
            description = """
                ### 검색어, 지원 상태 필터를 통해 지원 카드 목록을 조회합니다.<br><br>
                
                - **[페이징 방식]**<br>
                  - 커서 기반 페이징 사용<br>
                  - 한 페이지당 기본 20개의 지원 카드 조회<br>
                
                - **[요청 파라미터]**<br>
                  - **query**: 검색어 (선택 사항, 회사명 검색)<br>
                  - **applicationStatus**: 지원 상태 필터 (필수)<br>
                  - **lastCursorId**: 마지막으로 조회한 지원 카드 ID (첫 페이지는 null, 다음 페이지는 이전 응답의 nextCursorId 사용)<br><br>
                
                - **[사용 예시]**<br>
                  1. 첫 페이지 조회: /v1/applications?applicationStatus=ALL<br>
                  2. 다음 페이지 조회: /v1/applications?applicationStatus=ALL&lastCursorId=20<br>
                  3. 검색어 포함: /v1/applications?query=네이버&applicationStatus=ALL
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "지원 카드 목록 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationPageResponse.class)
            )
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationPageResponse> getApplications(
            @Parameter(
                    description = "검색어 (회사명, 포지션 등)",
                    example = "Backend"
            )
            String query,

            @Parameter(
                    description = "지원 상태 필터",
                    example = "ALL (기본값)",
                    required = true
            )
            ApplicationStatus applicationStatus,

            @Parameter(
                    description = "마지막으로 조회한 지원 카드 ID (다음 페이지 커서)",
                    example = "10"
            )
            Long lastCursorId,
            Long memberId
    );

    @Operation(
            summary = "지원 현황 통계 조회 - JWT O",
            description = """
                ## 사용자의 전체 지원 현황 통계를 조회합니다.
                
                #### 지원 통계 항목
                - 전체 지원서 수
                - 면접 전형중인 지원서 수
                - 서류 제출 필요한 지원서 수
                - 최종 합격한 지원서 수
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "지원 통계 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationStatisticsResponse.class)
            )
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationStatisticsResponse> getApplicationStatistics(
            Long memberId
    );

    @Operation(
            summary = "진행 중인 지원 내역 조회 - JWT O",
            description = """
                ## 마감되지 않은(진행 중인) 지원 카드 목록을 조회합니다.<br><br>
                - 다음 면접 날짜 정보는 포함되지 않습니다.<br><br>
                
                - **[페이징 방식]**<br>
                  - 커서 기반 페이징 사용<br>
                  - 한 페이지당 N(프론트와 협의) 개의 지원 카드 조회<br><br>
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "진행 중인 지원 내역 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationPageResponse.class)
            )
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationPageResponse> getInProgressApplications(
            Long memberId
    );

}