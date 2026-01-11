package org.squad.careerhub.domain.notification.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.squad.careerhub.domain.notification.controller.dto.NotificationPreferenceUpdateRequest;
import org.squad.careerhub.domain.notification.controller.dto.NotificationTokenRegisterRequest;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.service.dto.NotificationPageResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationPreferenceListResponse;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(
        name = "Notification API",
        description = "알림 및 FCM 토큰 관리 API 문서입니다."
)
public abstract class NotificationDocsController {

    @Operation(
            summary = "알림 목록 조회 (커서 기반 페이지네이션) - [JWT O]",
            description = """
                    ### 로그인한 사용자의 알림 목록을 조회합니다.
                    - Query 파라미터
                      - `cursorId`: 마지막으로 조회한 알림 ID (optional)
                      - `size`: 페이지 크기 (optional, default = 20)
                    - 응답
                      - `notifications`: 알림 목록
                      - `hasNext`: 다음 페이지 존재 여부
                      - `nextCursorId`: 다음 페이지 조회용 커서 ID
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "알림 목록 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = NotificationPageResponse.class)
            )
    )
    @ApiExceptions(values = {
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<NotificationPageResponse> getMyNotifications(
            @Parameter(description = "마지막으로 조회한 알림 ID", example = "101")
            @RequestParam(required = false)
            Long cursorId,

            @Parameter(description = "페이지 크기 (기본값 20)", example = "20")
            @RequestParam(required = false)
            Integer size,

            Long memberId
    );

    @Operation(
            summary = "알림 읽음 처리 - [JWT O]",
            description = """
                    ### 특정 알림을 읽음 처리합니다.
                    - 자신의 알림이 아닐 경우 403 Forbidden 응답이 내려가도록 구현합니다.
                    """,
            security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
            responseCode = "204",
            description = "알림 읽음 처리 성공"
    )
    @ApiExceptions(values = {
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.NOT_FOUND,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> readNotification(
            @Parameter(
                    description = "알림 ID",
                    required = true,
                    example = "101"
            )
            Long notificationId,
            Long memberId
    );


    @Operation(
            summary = "FCM 토큰 등록 - [JWT O]",
            description = """
                    ### 로그인한 사용자의 FCM 토큰을 등록/갱신합니다.
                    - 동일 회원 + deviceId 조합으로 이미 토큰이 있다면 갱신하도록 구현할 수 있습니다.
                    """
    )
    @ApiResponse(
            responseCode = "201",
            description = "FCM 토큰 등록/갱신 성공"
    )
    @ApiExceptions(values = {
            ErrorStatus.BAD_REQUEST,
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> registerDevice(
            @RequestBody(
                    description = "FCM 토큰 등록 요청 바디",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationTokenRegisterRequest.class)
                    )
            )
            NotificationTokenRegisterRequest request,
            Long memberId
    );


    @Operation(
            summary = "FCM 토큰 삭제 - [JWT O]",
            description = """
                    ### 로그인한 사용자의 FCM 토큰을 삭제합니다.
                    - 다른 사용자의 토큰을 삭제하려 할 경우 403 Forbidden 이 내려가도록 구현합니다.
                    """,
            security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
            responseCode = "204",
            description = "FCM 토큰 삭제 성공"
    )
    @ApiExceptions(values = {
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.NOT_FOUND,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> deleteNotificationToken(
            @Parameter(
                    description = "알림 토큰 ID",
                    required = true,
                    example = "10"
            )
            Long tokenId,
            Long memberId
    );

    @Operation(
            summary = "알림 전체 읽음 처리 - [JWT O]",
            description = "### 로그인한 사용자의 모든 알림을 읽음 처리합니다.",
            security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(responseCode = "204", description = "전체 읽음 처리 성공")
    @ApiExceptions(values = {
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> markAllAsRead(
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "알림 설정(이벤트) 업데이트 - [JWT O]",
            description = """
                    ### 특정 이벤트 알림 활성/비활성을 설정합니다.
                    """,
            security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(responseCode = "204", description = "알림 설정 업데이트 성공")
    @ApiExceptions(values = {
            ErrorStatus.BAD_REQUEST,
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> updatePreference(
            @Parameter(description = "알림 이벤트", required = true, example = "INTERVIEW_D1")
            @RequestBody(
                    description = "알림 설정 업데이트 요청",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationPreferenceUpdateRequest.class)
                    )
            )
            NotificationPreferenceUpdateRequest request,

            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "알림 삭제 - [JWT O]",
            description = """
                    ### 특정 알림을 삭제(논리삭제)합니다.
                    - 자신의 알림이 아닐 경우 403 Forbidden
                    """,
            security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(responseCode = "204", description = "알림 삭제 성공")
    @ApiExceptions(values = {
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.NOT_FOUND,
            ErrorStatus.FORBIDDEN_DELETE,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "알림 설정 목록 조회 - [JWT O]",
            description = """
                    ### 로그인한 사용자의 알림 설정(이벤트별 ON/OFF)을 조회합니다.
                    - DB에 설정 row가 없으면 기본 enabled=true 로 응답에 포함됩니다. (row 기반 sparse 저장)
                    """,
            security = {@SecurityRequirement(name = "Bearer")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "알림 설정 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = NotificationPreferenceListResponse.class)
            )
    )
    @ApiExceptions(values = {
            ErrorStatus.UNAUTHORIZED_ERROR,
            ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<NotificationPreferenceListResponse> getPreferences(
            @Parameter(description = "플랫폼", example = "WEB")
            @RequestParam(required = false, defaultValue = "WEB") NotificationPlatform platform,
            @Parameter(hidden = true) Long memberId
    );

}