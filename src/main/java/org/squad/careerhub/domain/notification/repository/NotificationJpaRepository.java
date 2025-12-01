package org.squad.careerhub.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.notification.entity.Notification;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

}