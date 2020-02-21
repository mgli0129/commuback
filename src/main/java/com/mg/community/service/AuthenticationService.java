package com.mg.community.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mg.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName AuthenticationService
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/19 14:02
 * @Version 1.0
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    /**
     * 使用jwt生成指定用户的token
     * @param user
     * @return
     */
    public String getToken(User user) {

        String token = JWT.create().withAudience(user.getId()+"")
                .sign(Algorithm.HMAC256(user.getId() + "9" + user.getGmtCreate()));
        return token;
    }

    /**
     * 通过http请求中的token获取user信息
     * @param request
     * @return
     */
    public User getUser(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token == null){
            return null;
        }
        String userId = JWT.decode(token).getAudience().get(0);
        if(userId==null){
            return null;
        }
        User user = userService.findById(Long.parseLong(userId));
        return user;
    }

}
