package org.squad.careerhub.domain.notification.service.dto;

import lombok.Builder;

@Builder
public record NotificationTokenParamRequest(
    Long memberId,
    String fcmToken,
    String deviceId,
    String deviceType,
    String osVersion,
    String appVersion
) {

    public static NotificationTokenParamRequest of(
        Long memberId,
        String fcmToken,
        String deviceId,
        String deviceType,
        String osVersion,
        String appVersion
    ) {
        return NotificationTokenParamRequest.builder()
            .memberId(memberId)
            .fcmToken(fcmToken)
            .deviceId(deviceId)
            .deviceType(deviceType)
            .osVersion(osVersion)
            .appVersion(appVersion)
            .build();
    }

//    public NotificationToken toEntity() {
//        return NotificationToken.builder()
//            .memberId(memberId)
//            .fcmToken(fcmToken)
//            .deviceId(deviceId)
//            .deviceType(deviceType)
//            .osVersion(osVersion)
//            .appVersion(appVersion)
//            .build();
//    }
}
