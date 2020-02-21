package com.mg.community.controller;

import com.mg.community.annotation.PassToken;
import com.mg.community.annotation.UserLoginToken;
import com.mg.community.common.OutputService;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.User;
import com.mg.community.service.UserService;
import com.mg.community.util.RSAUtil;
import com.mg.community.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description 用户操作
 * @Author MGLi
 * @Date 2020/2/21 15:30
 * @Version 1.0
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OutputService outputService;

    @Autowired
    private RedisUtil redisUtil;

    @PassToken
    @PostMapping("/api/user/login")
    public Object login(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        User user = userService.findByName(username);
        if (user == null) {
            throw new CustomizeException(CommonErrorCode.LOGIN_INVALID_USER_PASSWORD);
        }
        String dbPassword = user.getPwd();
        try {
            String privateKey = (String) redisUtil.get(RedisUtil.RSA_PRIVATE_KEY);
            String inputDecryptData = RSAUtil.decrypt(password,
                    RSAUtil.getPrivateKey(privateKey));
            String dbDecryptData = RSAUtil.decrypt(dbPassword,
                    RSAUtil.getPrivateKey(privateKey));
            if (!StringUtils.equals(dbDecryptData, inputDecryptData)) {
                throw new CustomizeException(CommonErrorCode.LOGIN_INVALID_USER_PASSWORD);
            }
        } catch (Exception e) {
            if(e instanceof IllegalArgumentException){
                throw new CustomizeException(CommonErrorCode.LOGIN_INVALID_USER_PASSWORD);
            }else {
                throw new CustomizeException(CommonErrorCode.RSA_ENCRYPT_DEENCRYPT_ERROR);
            }
        }

        //写入cookie
//        Cookie cookie = new Cookie("token", user.getToken());
//        cookie.setPath("/");
//        response.addCookie(cookie);

        //使用jwt替换上面cookie-session方式操作token
        //登录成功，在session里保存user信息
        request.getSession().setAttribute("user", user);

        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }

    @UserLoginToken
    @GetMapping("/api/user/logout")
    public Object logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().removeAttribute("user");
//        Cookie cookie = new Cookie("token", null);
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        outUni.put("common", null);

        return ResultDTO.okOf(outUni);
    }

    @PostMapping("/api/user/register")
    public Object register(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "phone", required = true) String phone,
            HttpServletRequest request) {
        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        User oldUser = userService.findByName(username);

        //用户名已被注册
        if (oldUser != null) {
            throw new CustomizeException(CommonErrorCode.USER_NAME_EXISTED);
        }

        boolean phoneRegisted = userService.findByPhone(phone);
        //手机号已被注册
        if (oldUser != null && phoneRegisted) {
            throw new CustomizeException(CommonErrorCode.USER_PHONE_EXISTED);
        }

        User user = new User();
        user.setAccountId(username);
        user.setName(username);
        user.setPwd(password);
        user.setPhone(phone);
        user.setAcctType("LOC");
        try {
            userService.createOrUpdate(user);
        } catch (Exception e) {
            throw new CustomizeException(CommonErrorCode.USER_CREATE_FAIL);
        }

        User newUser = userService.findByName(username);
        request.getSession().setAttribute("user", newUser);
        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }


}
