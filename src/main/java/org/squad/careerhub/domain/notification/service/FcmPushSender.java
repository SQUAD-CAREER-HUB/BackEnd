package org.squad.careerhub.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender implements PushSender {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void send(String token, String title, String body, String linkUrl) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .putData("title", title == null ? "" : title)
                    .putData("body", body == null ? "" : body)
                    .putData("linkUrl", linkUrl == null ? "" : linkUrl)
                    .build();

            firebaseMessaging.send(message);
        } catch (Exception e) {
            log.warn("[FcmPushSender] send failed. token={}, err={}", token, e.getMessage(), e);
        }
    }
}
