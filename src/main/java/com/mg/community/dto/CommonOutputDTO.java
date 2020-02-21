package com.mg.community.dto;

import com.mg.community.model.User;
import lombok.Data;

/**
 * @ClassName CommonOutputDTO
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/11 18:55
 * @Version 1.0
 */
@Data
public class CommonOutputDTO {
    private Long userId;
    private String userName;
    private String avatarUrl;
    private String token;
    private int countUnread;
    private Long expire;
}
