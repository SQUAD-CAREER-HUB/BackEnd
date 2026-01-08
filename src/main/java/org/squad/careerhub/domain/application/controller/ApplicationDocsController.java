package org.squad.careerhub.domain.application.controller;

import static org.squad.careerhub.global.error.ErrorStatus.BAD_REQUEST;
import static org.squad.careerhub.global.error.ErrorStatus.FORBIDDEN_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.INTERNAL_SERVER_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.NOT_FOUND;
import static org.squad.careerhub.global.error.ErrorStatus.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.ApplicationUpdateRequest;
import org.squad.careerhub.domain.application.entity.StageResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationCreationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationDetailPageResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.global.support.PageResponse;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Application", description = "지원서 관련 API 문서")
public abstract class ApplicationDocsController {

    @Operation(
            summary = "지원 카드 등록 - JWT O",
            description = """
                    ### 새로운 지원 카드를 등록합니다.
                    - 면접 일정 및 기타 전형 일정도 함께 등록할 수 있습니다.<br><br>
                    
                    - **[요청 형식]** <br>
                      - **Content-Type**: multipart/form-data<br>
                      - JSON 데이터와 파일을 함께 전송합니다.<br><br>
                    
                    - **[요청 파라미터]**<br>
                      - **request** : 지원 카드 등록 데이터 (JSON 형식, Part name: "request")<br>
                      - **files**: 첨부 파일 목록 (선택 사항, 최대 5개, Part name: "files")<br><br>
                    
                    - **[제약 사항]** <br>
                        - 동일한 채용 공고에 대해 중복 지원할 수 없습니다.<br>
                        - 면접 일정은 면접 전형 단계가 있을 때만 등록할 수 있습니다.<br>
                        - 기타 전형 일정은 기타 전형 단계가 있을 때만 등록할 수 있습니다.<br>
                    """
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
            @Parameter(
                    description = "첨부 파일 목록 (선택 사항, 최대 5개)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            List<MultipartFile> files,
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
                    - 지원서 기본 정보, 전형 단계별 타임라인, 첨부 파일 정보 등을 포함합니다.
                    - 내 면접 질문은 따로 API가 존재합니다.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "지원 카드 상세 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApplicationDetailPageResponse.class)
            )
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            FORBIDDEN_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationDetailPageResponse> findApplication(
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
                    ### 검색어, 전형 단계, 서류 상태, 전형 결과 필터를 통해 지원 카드 목록을 조회합니다.<br><br>
                    
                    - **[페이징 방식]**<br>
                      - 커서 기반 페이징 사용<br>
                      - 한 페이지당 기본 20개의 지원 카드 조회<br>
                    
                    - **[요청 파라미터]**<br>
                      - **query**: 검색어 (선택 사항, 회사명 또는 직무명 검색)<br>
                      - **stageType**: 전형 단계 필터 (선택 사항)<br>
                      - **submissionStatus**: 서류 상태 필터 (선택 사항, 서류 전형일 경우 유효합니다)<br>
                      - **stageResult**: 전형 결과 필터 (선택 사항)<br>
                      - **lastCursorId**: 마지막으로 조회한 지원 카드 ID (첫 페이지는 null, 다음 페이지는 이전 응답의 nextCursorId 사용)<br>
                      - **size**: 한 페이지당 조회할 지원 카드 개수 (기본값 20 10 <= size <=30)<br><br>
                    
                    - **[사용 예시]**<br>
                      1. 첫 페이지 조회: /v1/applications?size=20<br>
                      2. 다음 페이지 조회: /v1/applications?lastCursorId=20&size=20<br>
                      3. 검색어 포함: /v1/applications?query=네이버&size=20<br>
                      4. 지원 상태 필터링: /v1/applications?stageType=INTERVIEW&stageType=ETC&size=20<br>
                      5. 제출 상태 필터링: /v1/applications?submissionStatus=SUBMITTED&size=20<br>
                      6. 전형 결과 필터링: /v1/applications?stageResult=STAGE_PASS&size=20
                      7. 복합 필터링: /v1/applications?query=네이버&stageType=INTERVIEW&stageType=ETC&stageResult=STAGE_PASS&size=20
                    """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            BAD_REQUEST,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PageResponse<ApplicationSummaryResponse>> findApplications(
            @Parameter(
                    description = "검색어 (회사명, 직무)",
                    example = "Backend"
            )
            String query,
            @Parameter(
                    description = "현재 지원서 전형 단계",
                    example = "DOCUMENT"
            )
            List<StageType> stageType,
            @Parameter(
                    description = "제출 상태",
                    example = "NOT_SUBMITTED"
            )
            List<SubmissionStatus> submissionStatus,
            @Parameter(
                    description = "전형 결과",
                    example = "STAGE_PASS"
            )
            List<StageResult> stageResult,
            @Parameter(
                    description = "마지막으로 조회한 지원 카드 ID (다음 페이지 커서)",
                    example = "10"
            )
            Long lastCursorId,
            @Parameter(
                    description = "한 페이지당 조회할 지원 카드 개수",
                    example = "20"
            )
            int size,
            Long memberId
    );

    @Operation(
            summary = "지원 현황 통계 조회 - JWT O",
            description = """
                    ## 사용자의 전체 지원 현황 통계를 조회합니다.
                    
                    #### 지원 통계 항목
                    - 전체 지원서 수
                    - 서류 전형 단계인 지원서 수
                    - 면접 전형 단계인 지원서 수
                    - 기타 전형 단계인 지원서 수
                    - 최종 합격한 지원서 수
                    - 최종 불합격한 지원서 수
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
            NOT_FOUND,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationStatisticsResponse> getApplicationStatistics(Long memberId);

    @Operation(
            summary = "마감되지 않은 서류 전형 지원서 내역 조회 - JWT O",
            description = """
                    ## 마감되지 않은 서류 전형 지원서 목록을 조회합니다.<br><br>
                    
                    - **[페이징 방식]**<br>
                      - 커서 기반 페이징 사용<br>
                      - 기본값: 10개의 지원 카드 조회<br><br>
                    """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            FORBIDDEN_ERROR,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<PageResponse<BeforeDeadlineApplicationResponse>> findBeforeDeadlineApplications(
            @Parameter(
                    description = "마지막으로 조회한 지원 카드 ID (다음 페이지 커서)",
                    example = "10"
            )
            Long lastCursorId,
            @Parameter(
                    description = "한 페이지당 조회할 지원 카드 개수",
                    example = "10"
            )
            int size,
            Long memberId
    );

    @Operation(
            summary = "주간/월간 생성된 지원서 통계 조회 - JWT O",
            description = """
                    ## 최근 N주/N개월간 생성된 지원서 개수를 조회합니다.<br><br>
                    
                    - **[주간 통계]**<br>
                      - weekCount: 조회할 주 개수 (기본값: 6주)<br>
                      - 이번 주(월~일)가 제일 마지막<br>
                      - 기간 형식: "MM.DD - MM.DD"<br><br>
                      
                    - **[월간 통계]**<br>
                      - monthCount: 조회할 월 개수 (기본값: 6개월)<br>
                      - 이번 달이 제일 마지막<br>
                      - 기간 형식: "YYYY.MM"<br><br>
                      
                    - **[사용 예시]**<br>
                      - 기본: /v1/applications/statistics/creation (6주, 6개월)<br>
                      - 4주간: /v1/applications/statistics/creation?weekCount=4<br>
                      - 12개월간: /v1/applications/statistics/creation?monthCount=12<br>
                      - 커스텀: /v1/applications/statistics/creation?weekCount=8&monthCount=3<br>
                    """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            FORBIDDEN_ERROR,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<ApplicationCreationStatisticsResponse> getApplicationCreationStatistics(
            @Parameter(
                    description = "조회할 주 개수 (기본값: 6주, 최소: 1, 최대: 12)",
                    example = "6"
            )
            int weekCount,
            @Parameter(
                    description = "조회할 월 개수 (기본값: 6개월, 최소: 1, 최대: 12)",
                    example = "6"
            )
            int monthCount,
            Long memberId
    );
}