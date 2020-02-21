package com.mg.community.controller;

import com.mg.community.annotation.UserLoginToken;
import com.mg.community.common.OutputService;
import com.mg.community.dto.ResultDTO;
import com.mg.community.enums.NotificationTypeEnum;
import com.mg.community.exception.CommunityErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.Notification;
import com.mg.community.model.User;
import com.mg.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private OutputService outputService;

    @UserLoginToken
    @RequestMapping(value = "/api/notify/{id}", method = RequestMethod.GET)
    public Object readNotifyMessage(@PathVariable("id") Long id, HttpServletRequest request){

        //标记通知已读
        notificationService.readNotify(id);
        Notification notification = notificationService.findById(id);
        if(NotificationTypeEnum.nameOfType(notification.getType()) != null){
            //输出格式测试
            Map<String, Object> outUni = new HashMap<String, Object>();
            outUni.put("id", notification.getQuestionid());
            outUni.put("common", outputService.getCommonOutput(request));
            return ResultDTO.okOf(outUni);
        }else{
            throw new CustomizeException(CommunityErrorCode.TYPE_PARAM_NOT_FOUND);
        }
    }

    @UserLoginToken
    @RequestMapping(value = "/api/clear", method = RequestMethod.GET)
    public Object clearNotifications(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            return ResultDTO.errorOf(CommunityErrorCode.TOKEN_SESSION_HAS_EXPIRED);
//        }
        notificationService.clearByReceiver(user);

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }
}
