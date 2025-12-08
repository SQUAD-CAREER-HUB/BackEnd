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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.notification.controller.dto.NotificationPageResponse;
import org.squad.careerhub.domain.notification.controller.dto.NotificationTokenRequest;
import org.squad.careerhub.domain.notification.service.NotificationService;
import org.squad.careerhub.global.annotation.LoginMember;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController extends NotificationDocsController {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public ResponseEntity<NotificationPageResponse> getNotifications(
        @RequestParam(value = "lastCursorId", required = false) Long lastCursorId,
        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(NotificationPageResponse.mock());
    }


    @Override
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
        @PathVariable("notificationId") Long notificationId,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/tokens")
    public ResponseEntity<Void> registerNotificationToken(
        @Valid @RequestBody NotificationTokenRequest request,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @DeleteMapping("/tokens/{tokenId}")
    public ResponseEntity<Void> deleteNotificationToken(
        @PathVariable("tokenId") Long tokenId,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }
}