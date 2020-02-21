package com.mg.community.common;

import com.mg.community.dto.CommonOutputDTO;
import com.mg.community.model.User;
import com.mg.community.service.AuthenticationService;
import com.mg.community.service.NotificationService;
import com.mg.community.service.UserService;
import com.mg.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName OutputService
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/11 18:59
 * @Version 1.0
 */
@Component
public class OutputService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RedisUtil redisUtil;

    public CommonOutputDTO getCommonOutput(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            return null;
        }
        String token = authenticationService.getToken(user);
        //存入Reids并设置过期时间
        if(redisUtil.testConnection()) {
            redisUtil.set(redisUtil.TOKEN+token, token);
            redisUtil.expire(redisUtil.TOKEN+token, redisUtil.TOKEN_24H, TimeUnit.HOURS);
        }
        System.out.println(token);
        int countUnread = notificationService.countUnread(user.getId());
        CommonOutputDTO commonOutputDTO = new CommonOutputDTO();
        commonOutputDTO.setUserId(user.getId());
        commonOutputDTO.setUserName(user.getAccountId());
        commonOutputDTO.setAvatarUrl(user.getAvatarUrl());
        commonOutputDTO.setCountUnread(countUnread);
        commonOutputDTO.setToken(token);
        return commonOutputDTO;
    }

}
