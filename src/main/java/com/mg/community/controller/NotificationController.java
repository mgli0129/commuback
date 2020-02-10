package com.mg.community.controller;

import com.mg.community.dto.ResultDTO;
import com.mg.community.enums.NotificationTypeEnum;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.model.Notification;
import com.mg.community.model.User;
import com.mg.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/notify/{id}", method = RequestMethod.GET)
    public Object readNotifyMessage(@PathVariable("id") Long id, HttpServletRequest request){
        Object user = request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        //标记通知已读
        notificationService.readNotify(id);
        Notification notification = notificationService.findById(id);
        if(NotificationTypeEnum.nameOfType(notification.getType()) != null){
            //输出格式测试
            Map<String, Object> outUni = new HashMap<String, Object>();
            outUni.put("id", notification.getQuestionid());
            return ResultDTO.okOf(outUni);
        }else{
            return ResultDTO.errorOf(CustomizeErrorCode.TYPE_PARAM_NOT_FOUND);
        }
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    public Object clearNotifications(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        notificationService.clearByReceiver(user);
        return ResultDTO.okOf();
    }
}
