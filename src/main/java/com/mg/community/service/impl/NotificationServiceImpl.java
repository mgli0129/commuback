package com.mg.community.service.impl;

import com.mg.community.dto.NotificationDTO;
import com.mg.community.enums.NotificationStatusEnum;
import com.mg.community.enums.NotificationTypeEnum;
import com.mg.community.mapper.NotificationMapper;
import com.mg.community.model.Notification;
import com.mg.community.model.NotificationExample;
import com.mg.community.model.User;
import com.mg.community.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("NotificationService")
public class NotificationServiceImpl implements NotificationService {

    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    @Override
    public void create(Notification notification) {
        if (notification != null) {
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notificationMapper.insert(notification);
        }
    }

    @Override
    public List<NotificationDTO> findNotificationByReceiver(Long receiver) {
        if (receiver == null) {
            return new ArrayList<>();
        }
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(receiver).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExample(notificationExample);

        List<NotificationDTO> notificationDTOS = notifications.stream().map(notification -> {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setNotificationTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTO.setNotificationStatusName(NotificationStatusEnum.nameOfStatus(notification.getStatus()));
            return notificationDTO;
        }).collect(Collectors.toList());

        return notificationDTOS;
    }

    @Override
    public int countUnread(Long receiver) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(receiver).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        List<Notification> notifications = notificationMapper.selectByExample(notificationExample);
        int countUnread = notifications.size();
        return countUnread;
    }

    @Override
    public void readNotify(Long id) {
        if (id != null) {
            Notification notification = new Notification();
            notification.setId(id);
            notification.setStatus(NotificationStatusEnum.READ.getStatus());
            NotificationExample notificationExample = new NotificationExample();
            notificationExample.createCriteria().andIdEqualTo(id);
            notificationMapper.updateByExampleSelective(notification, notificationExample);
        }
    }

    @Override
    public Notification findById(Long id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andIdEqualTo(id);
        List<Notification> notifications = notificationMapper.selectByExample(notificationExample);
        if(notifications == null){
            return null;
        }
        return notifications.get(0);
    }

    @Override
    public void clearByReceiver(User user) {
        Long receiver = user.getId();
        if (receiver != null) {
            Notification notification = new Notification();
            notification.setStatus(NotificationStatusEnum.READ.getStatus());
            NotificationExample notificationExample = new NotificationExample();
            notificationExample.createCriteria().andReceiverEqualTo(receiver);
            notificationMapper.updateByExampleSelective(notification, notificationExample);
        }
    }
}

