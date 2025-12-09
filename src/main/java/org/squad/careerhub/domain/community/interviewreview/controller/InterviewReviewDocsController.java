package org.squad.careerhub.domain.community.interviewreview.controller;

import static org.squad.careerhub.global.error.ErrorStatus.BAD_REQUEST;
import static org.squad.careerhub.global.error.ErrorStatus.FORBIDDEN_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.INTERNAL_SERVER_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.NOT_FOUND;
import static org.squad.careerhub.global.error.ErrorStatus.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewCreateRequest;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewReportRequest;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewUpdateRequest;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewDetailResponse;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewSummaryResponse;
import org.squad.careerhub.global.support.PageResponse;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Review", description = "면접 후기 관련 API 문서")
public abstract class InterviewReviewDocsController {

        @Operation(
                summary = "면접 후기 등록 - JWT O",
                description = "새로운 면접 후기를 등록합니다."
        )
        @RequestBody(
                description = "면접 후기 등록 요청",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ReviewCreateRequest.class)
                )
        )
        @ApiResponse(
                responseCode = "201",
                description = "면접 후기 등록 성공"
        )
        @ApiExceptions(values = {
                UNAUTHORIZED_ERROR,
                BAD_REQUEST,
                INTERNAL_SERVER_ERROR
        })
        public abstract ResponseEntity<Void> create(
                ReviewCreateRequest request,
                Long memberId
        );

        @Operation(
                summary = "면접 후기 목록 조회 - JWT O",
                description = """
                ## 검색어와 정렬 기준을 이용해 면접 후기 목록을 조회합니다.<br><br>
                
                - **[페이징 방식]**<br>
                  - 커서 기반 페이징 사용<br>
                  - 한 페이지당 기본 20개의 면접 후기 조회<br><br>
                
                - **[요청 파라미터]**<br>
                  - **query**: 검색어 (선택 사항, 회사명 검색)<br>
                  - **sort**: 정렬 기준 (NEWEST: 최신순, OLDEST: 오래된순)<br>
                  - **lastReviewId**: 마지막으로 조회한 면접 후기 ID (첫 페이지는 null)<br>
                  - **size**: 한 페이지당 조회할 면접 후기 개수 (기본값 20)<br><br>
                
                - **[정렬 방식]**<br>
                  - **NEWEST**: 최신순 (작성일 기준)<br>
                  - **OLDEST**: 오래된순 (작성일 기준)<br>
                
                - **[사용 예시]**<br>
                  1. 첫 페이지 조회 (최신순): /v1/reviews?sort=NEWEST&size=20<br>
                  2. 다음 페이지 조회 (최신순): /v1/reviews?sort=NEWEST&lastReviewId=20&size=20<br>
                  3. 첫 페이지 조회 (오래된순): /v1/reviews?sort=OLDEST&size=20<br>
                  4. 다음 페이지 조회 (오래된순): /v1/reviews?sort=OLDEST&lastReviewId=20&size=20<br>
                  5. 검색어 포함: /v1/reviews?query=네이버&sort=NEWEST&size=20
                """
        )
        @ApiResponse(
                responseCode = "200",
                description = "면접 후기 목록 조회 성공"
        )
        @ApiExceptions(values = {
                UNAUTHORIZED_ERROR,
                BAD_REQUEST,
                INTERNAL_SERVER_ERROR
        })
        public abstract ResponseEntity<PageResponse<ReviewSummaryResponse>> getReviews(
                @Parameter(description = "검색어 (회사명 검색)", example = "네이버")
                String query,

                @Parameter(
                        description = "정렬 기준 (NEWEST: 최신순, LIKE_DESC: 추천순)",
                        example = "NEWEST",
                        required = true
                )
                SortType sort,

                @Parameter(description = "마지막으로 조회한 면접 후기 ID (다음 페이지 커서)", example = "20")
                Long lastReviewId,

                @Parameter(
                        description = "한 페이지당 조회할 면접 후기 개수",
                        example = "20"
                )
                int size
        );


        @Operation(
                summary = "면접 후기 상세 조회 - JWT O",
                description = "특정 면접 후기에 대한 상세 정보를 조회합니다."
        )
        @ApiResponse(
                responseCode = "200",
                description = "면접 후기 상세 조회 성공",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ReviewDetailResponse.class)
                )
        )
        @ApiExceptions(values = {
                UNAUTHORIZED_ERROR,
                NOT_FOUND,
                INTERNAL_SERVER_ERROR
        })
        public abstract ResponseEntity<ReviewDetailResponse> getReview(
                @Parameter(
                        description = "면접 후기 ID",
                        example = "1",
                        required = true
                )
                Long reviewId,
                Long memberId
        );

        @Operation(
                summary = "면접 후기 수정 - JWT O",
                description = """
                        ### 기존에 등록된 면접 후기를 수정합니다.<br>
                        - 본인이 작성한 후기만 수정할 수 있습니다.
                        """
        )
        @RequestBody(
                description = "면접 후기 수정 요청",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ReviewUpdateRequest.class)
                )
        )
        @ApiResponse(
                responseCode = "204",
                description = "면접 후기 수정 성공"
        )
        @ApiExceptions(values = {
                UNAUTHORIZED_ERROR,
                FORBIDDEN_ERROR,
                BAD_REQUEST,
                NOT_FOUND,
                INTERNAL_SERVER_ERROR
        })
        public abstract ResponseEntity<Void> update(
                ReviewUpdateRequest request,
                @Parameter(
                        description = "면접 후기 ID",
                        example = "1",
                        required = true
                )
                Long reviewId,
                Long memberId
        );

        @Operation(
                summary = "면접 후기 삭제 - JWT O",
                description = """
                        ### 등록된 면접 후기를 삭제합니다.<br>
                        - 본인이 작성한 후기만 삭제할 수 있습니다.
                        """
        )
        @ApiResponse(
                responseCode = "204",
                description = "면접 후기 삭제 성공"
        )
        @ApiExceptions(values = {
                UNAUTHORIZED_ERROR,
                FORBIDDEN_ERROR,
                NOT_FOUND,
                INTERNAL_SERVER_ERROR
        })
        public abstract ResponseEntity<Void> delete(
                @Parameter(
                        description = "면접 후기 ID",
                        example = "1",
                        required = true
                )
                Long reviewId,
                Long memberId
        );

        @Operation(
                summary = "면접 후기 신고 - JWT O",
                description = "부적절한 면접 후기를 신고합니다."
        )
        @RequestBody(
                description = "후기 신고 요청",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ReviewReportRequest.class)
                )
        )
        @ApiResponse(
                responseCode = "201",
                description = "면접 후기 신고 접수 성공"
        )
        @ApiExceptions(values = {
                UNAUTHORIZED_ERROR,
                BAD_REQUEST,
                NOT_FOUND,
                INTERNAL_SERVER_ERROR
        })
        public abstract ResponseEntity<Void> report(
                ReviewReportRequest request,
                @Parameter(
                        description = "면접 후기 ID",
                        example = "1",
                        required = true
                )
                Long reviewId,
                Long memberId
        );

}