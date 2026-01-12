package org.squad.careerhub.domain.notification.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationPlatform {
    WEB("웹"),
    ANDROID("안드로이드"),
    IOS("iOS");

    private final String description;
}
