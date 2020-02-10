package com.mg.community.enums;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName StatusEnum
 * @Description 过期时间枚举
 * @Author MGLi
 * @Date 2020/1/22 16:23
 * @Version 1.0
 */

public abstract class StatusEnum {

    /**
     * 过期时间相关枚举
     */
    public static enum ExpireEnum {
        //未读消息的有效期为30天
        UNREAD_MSG(30L, TimeUnit.DAYS);

        /**
         * 过期时间
         */
        private Long time;
        /**
         * 时间单位
         */
        private TimeUnit timeUnit;

        ExpireEnum(Long time, TimeUnit timeUnit) {
            this.time = time;
            this.timeUnit = timeUnit;
        }

        public Long getTime() {
            return time;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
    }
}
