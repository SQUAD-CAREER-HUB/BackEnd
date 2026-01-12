package org.squad.careerhub.domain.notification.service;

public interface PushSender {

    void send(String token, String title, String body, String linkUrl);
}