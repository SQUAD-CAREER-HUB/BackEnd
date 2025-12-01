package org.squad.careerhub.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.notification.service.NotificationService;

@RequiredArgsConstructor
@RestController
public class NotificationController extends NotificationDocsController {

    private final NotificationService notificationService;

}