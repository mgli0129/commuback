package com.mg.community.controller;

import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.model.User;
import com.mg.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Object dologin(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            HttpServletResponse response) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        User user = userService.findByName(username);
        if (user == null || !password.equals(user.getPwd())) {
            return ResultDTO.errorOf(CustomizeErrorCode.INVALID_USER_PASSWORD);
        }

        //写入cookie
        Cookie cookie = new Cookie("token", user.getToken());
        response.addCookie(cookie);
        outUni.put("token", user.getToken());

        return ResultDTO.okOf(outUni);
    }
}
