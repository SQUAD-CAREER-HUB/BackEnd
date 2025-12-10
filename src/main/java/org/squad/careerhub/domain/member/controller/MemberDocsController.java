package org.squad.careerhub.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.member.service.dto.MemberActivityPageResponse;
import org.squad.careerhub.domain.member.service.dto.MemberProfileResponse;
import org.squad.careerhub.domain.member.controller.dto.MemberProfileUpdateRequest;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(
    name = "Member API",
    description = "회원 프로필 및 활동 이력 관리 API 문서입니다."
)
public abstract class MemberDocsController {
    @Operation(
        summary = "마이 프로필 조회 - [JWT O]",
        description = """
                    ### 로그인한 회원의 프로필 정보를 조회합니다.
                    - 닉네임, 프로필 이미지, 역할(ROLE_MEMBER 등)을 반환합니다.
                    - 소셜 로그인으로 가입한 회원 기준입니다.
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "200",
        description = "프로필 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MemberProfileResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<MemberProfileResponse> getMyProfile(Long memberId);

    @Operation(
        summary = "마이 프로필 수정 - [JWT O]",
        description = """
                    ### 로그인한 회원의 프로필 정보를 수정합니다.
                    - 수정 가능 항목:
                      - nickname: 닉네임
                    - 제약:
                      - 닉네임은 중복 불가 정책 등을 서비스 레벨에서 검증할 수 있습니다.
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "200",
        description = "프로필 수정 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MemberProfileResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<MemberProfileResponse> updateMyProfile(
        @RequestBody(
            description = "프로필 수정 요청 바디",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = MemberProfileUpdateRequest.class)
            )
        )
        MemberProfileUpdateRequest request,
        Long memberId
    );

    @Operation(
        summary = "최근 활동 조회 (커서 기반 페이지네이션) - [JWT O]",
        description = """
                    ### 로그인한 회원의 최근 활동 이력을 조회합니다.
                    - 예시 활동:
                      - 지원 카드 생성/수정
                      - 면접 일정 생성/수정
                      - 면접 후기 작성
                    - 커서 기반 페이지네이션:
                      - lastCursorId: 마지막으로 조회한 activityId
                      - size: 페이지 크기 (기본 20)
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "200",
        description = "최근 활동 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = MemberActivityPageResponse.class)
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<MemberActivityPageResponse> getMyActivities(
        @Parameter(
            description = "마지막으로 조회한 활동 ID (커서 기반 페이지네이션)",
            example = "10",
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
        summary = "회원 탈퇴 - [JWT O]",
        description = """
                    ### 로그인한 회원이 서비스에서 탈퇴합니다.
                    - 회원 정보는 soft delete(`deletedAt` 설정)로 처리됩니다.
                    - 탈퇴 이후에는 재로그인 시 신규 가입 플로우를 탈 수 있습니다.
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "204",
        description = "회원 탈퇴 성공 (응답 본문 없음)"
    )
    @ApiExceptions(values = {
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.FORBIDDEN_DELETE,   // 실제 enum 이름에 맞게 조정
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> withdraw(Long memberId);
}