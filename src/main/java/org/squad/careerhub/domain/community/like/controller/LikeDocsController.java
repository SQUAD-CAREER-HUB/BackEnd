package org.squad.careerhub.domain.community.like.controller;

import static org.squad.careerhub.global.error.ErrorStatus.BAD_REQUEST;
import static org.squad.careerhub.global.error.ErrorStatus.INTERNAL_SERVER_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.NOT_FOUND;
import static org.squad.careerhub.global.error.ErrorStatus.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.community.like.entity.LikeType;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Like", description = "좋아요 관련 API 문서")
public abstract class LikeDocsController {

    @Operation(
            summary = "좋아요 등록 - JWT O",
            description = """
                    ### 특정 리소스(면접 후기, 댓글 등)에 좋아요를 등록합니다.<br><br>

                    - **[요청 파라미터]**<br>
                      - **targetId**: 좋아요를 등록할 대상 ID (예: 후기 ID, 댓글 ID)<br>
                      - **likeType**: 좋아요 대상 타입<br><br>

                    - **[사용 예시]**<br>
                      - /v1/likes/1?likeType=REVIEW<br>
                      - /v1/likes/10?likeType=COMMENT
                    """
    )
    @ApiResponse(
            responseCode = "201",
            description = "좋아요 등록 성공"
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            BAD_REQUEST,
            NOT_FOUND,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> add(
            @Parameter(
                    description = "좋아요를 등록할 대상 ID",
                    example = "1",
                    required = true
            )
            Long targetId,

            @Parameter(
                    description = "좋아요 대상 타입",
                    example = "REVIEW",
                    required = true
            )
            LikeType likeType,
            Long memberId
    );

    @Operation(
            summary = "좋아요 취소 - JWT O",
            description = """
                    ### 특정 리소스에 등록된 좋아요를 취소합니다.<br><br>

                    - **[요청 파라미터]**<br>
                      - **targetId**: 좋아요를 취소할 대상 ID<br>
                      - **likeType**: 좋아요 대상 타입 <br><br>

                    - **[사용 예시]**<br>
                      - /v1/likes/1?likeType=REVIEW<br>
                      - /v1/likes/10?likeType=COMMENT
                    """
    )
    @ApiResponse(
            responseCode = "204",
            description = "좋아요 취소 성공"
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            UNAUTHORIZED_ERROR,
            NOT_FOUND,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> remove(
            @Parameter(
                    description = "좋아요를 취소할 대상 ID",
                    example = "1",
                    required = true
            )
            Long targetId,

            @Parameter(
                    description = "좋아요 대상 타입",
                    example = "REVIEW",
                    required = true
            )
            LikeType likeType,
            Long memberId
    );

}