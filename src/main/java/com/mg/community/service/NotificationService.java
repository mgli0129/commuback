package com.mg.community.service;

import com.mg.community.dto.NotificationDTO;
import com.mg.community.model.Notification;
import com.mg.community.model.User;

import java.util.List;

public interface NotificationService {
    void create(Notification notification);

    List<NotificationDTO> findNotificationByReceiver(Long receiver);

    int countUnread(Long receiver);

    void readNotify(Long id);

    Notification findById(Long id);

    void clearByReceiver(User user);
}
