package com.mg.community.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifier;
    private Long receiver;
    private Integer type;
    private Long outerid;
    private Integer status;
    private Long gmtCreate;
    private String notifierName;
    private String notifyTitle;
    private Long questionid;

    private String notificationTypeName;
    private String notificationStatusName;

}
