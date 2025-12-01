package org.squad.careerhub.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.notification.entity.Notification;
import org.squad.careerhub.domain.notification.entity.NotificationToken;

public interface NotificationTokenJpaRepository extends JpaRepository<NotificationToken, Long> {

}