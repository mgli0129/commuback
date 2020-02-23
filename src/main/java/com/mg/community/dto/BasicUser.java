package com.mg.community.dto;

import lombok.Data;

/**
 * @ClassName BasicUser
 * @Description 仅作为输出的User对象，按需输出，详细的内容要去User里获取
 * @Author MGLi
 * @Date 2020/2/23 10:12
 * @Version 1.0
 */
@Data
public class BasicUser {

    private Long id;
    private String accountId;
    private String name;
    private String avatarUrl;

}
