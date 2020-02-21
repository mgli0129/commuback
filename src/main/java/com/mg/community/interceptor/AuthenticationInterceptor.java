package com.mg.community.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mg.community.annotation.PassToken;
import com.mg.community.annotation.UserLoginToken;
import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.User;
import com.mg.community.service.UserService;
import com.mg.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @ClassName AuthenticationInterceptor
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/19 13:27
 * @Version 1.0
 */
@Service
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }

        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                //执行认证
                if (token == null) {
                    throw new CustomizeException(CommonErrorCode.TOKEN_NULL);
                }

                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new CustomizeException(CommonErrorCode.TOKEN_INVALID);
                }

                User user = userService.findById(Long.parseLong(userId));
                if (user == null) {
                    throw new CustomizeException(CommonErrorCode.TOKEN_USER_NOT_FOUND);
                }

                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getId() + "9" + user.getGmtCreate())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    throw new CustomizeException(CommonErrorCode.TOKEN_INVALID);
                }

                // 验证token是否过期
                if (redisUtil.testConnection()) {
                    if (!redisUtil.hasKey(redisUtil.TOKEN + token)) {
                        throw new CustomizeException(CommonErrorCode.TOKEN_HAS_EXPIRED);
                    }
                }

                // session是否已经失效
                User sessionUser = (User) request.getSession().getAttribute("user");
                if(sessionUser == null || sessionUser.getId() != user.getId()){
                    throw new CustomizeException(CommonErrorCode.TOKEN_SESSION_HAS_EXPIRED);
                }

                return true;
            }
        }
        return true;
    }
}
