package com.mg.community.interceptor;

import com.mg.community.model.User;
import com.mg.community.service.NotificationService;
import com.mg.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //用户验证
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("token")) {
                    User user = userService.findByToken(c.getValue());
                    if(user != null){
                        //获取未读数通知
                        int countUnread = notificationService.countUnread(user.getId());
                        request.getSession().setAttribute("user", user);
                        request.getSession().setAttribute("countUnread", countUnread);
                    }
                    break;
                }
            }
        }
        return true;
    }
}
