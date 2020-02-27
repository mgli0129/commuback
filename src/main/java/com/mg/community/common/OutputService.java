package com.mg.community.common;

import com.mg.community.dto.CommonOutputDTO;
import com.mg.community.model.User;
import com.mg.community.service.AuthenticationService;
import com.mg.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

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

    //公共输出
    public CommonOutputDTO getCommonOutput(HttpServletRequest request, String token) {

        User sessionUserByToken = authenticationService.getSessionUserByToken(token);
        if(sessionUserByToken == null){
            return null;
        }

        int countUnread = notificationService.countUnread(sessionUserByToken.getId());
        CommonOutputDTO commonOutputDTO = new CommonOutputDTO();
        commonOutputDTO.setUserId(sessionUserByToken.getId());
        commonOutputDTO.setUserName(sessionUserByToken.getAccountId());
        commonOutputDTO.setAvatarUrl(sessionUserByToken.getAvatarUrl());
        commonOutputDTO.setCountUnread(countUnread);
        commonOutputDTO.setToken(token);
        return commonOutputDTO;
    }

}
