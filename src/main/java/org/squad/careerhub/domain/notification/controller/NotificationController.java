package org.squad.careerhub.domain.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.notification.controller.dto.NotificationPreferenceUpdateRequest;
import org.squad.careerhub.domain.notification.controller.dto.NotificationTokenRegisterRequest;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.service.NotificationService;
import org.squad.careerhub.domain.notification.service.dto.NotificationPageResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationPreferenceListResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController extends NotificationDocsController {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public ResponseEntity<NotificationPageResponse> getMyNotifications(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") Integer size,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(notificationService.getMyNotifications(cursorId, size, memberId));
    }


    @Override
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable("notificationId") Long notificationId,
            @LoginMember Long memberId
    ) {
        notificationService.markAsRead(memberId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@LoginMember Long memberId) {
        notificationService.markAllAsRead(memberId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("/preferences")
    public ResponseEntity<Void> updatePreference(
            @Valid @RequestBody NotificationPreferenceUpdateRequest request,
            @LoginMember Long memberId
    ) {
        notificationService.updatePreference(
                memberId,
                request.toUpdatePreference()
        );
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceListResponse> getPreferences(
            @RequestParam(required = false, defaultValue = "WEB") NotificationPlatform platform,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(notificationService.getPreferences(memberId, platform));
    }

    @Override
    @PostMapping("/devices")
    public ResponseEntity<Void> registerDevice(
            @Valid @RequestBody NotificationTokenRegisterRequest request,
            @LoginMember Long memberId
    ) {
        notificationService.registerDevice(
                memberId,
                request.toNewDeviceToken()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable("notificationId") Long notificationId,
            @LoginMember Long memberId
    ) {
        notificationService.deleteNotification(notificationId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/tokens/{tokenId}")
    public ResponseEntity<Void> deleteNotificationToken(
            @PathVariable("tokenId") Long tokenId,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    public ResponseEntity<Void> sendTest(
            @RequestParam(defaultValue = "WEB") NotificationPlatform platform,
            @LoginMember Long memberId
    ) {
        notificationService.sendTest(memberId, platform);
        return ResponseEntity.accepted().build(); // 202
    }
}