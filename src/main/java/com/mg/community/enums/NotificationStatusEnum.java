package com.mg.community.enums;

public enum NotificationStatusEnum {
    UNREAD(0,"未读"),
    READ(1,"已读"),
    ;

    private int status;
    private String name;

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    NotificationStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public static String nameOfStatus(int status){
        for (NotificationStatusEnum notificationStatusEnum : NotificationStatusEnum.values()) {
            if(notificationStatusEnum.getStatus() == status){
                return notificationStatusEnum.getName();
            }
        }
        return "";
    }
}
