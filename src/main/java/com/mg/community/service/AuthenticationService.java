package com.mg.community.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.User;
import com.mg.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AuthenticationService
 * @Description Token处理服务类
 * 特别说明：
 * ·user - 从数据库中获取的；
 * ·sessionUser - 从Redis中获取的；
 * @Author MGLi
 * @Date 2020/2/19 14:02
 * @Version 1.0
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 使用jwt生成指定用户的token
     *
     * @param user
     * @return
     */
    public String genToken(User user) {

        String token = JWT.create().withAudience(user.getId() + "")
                .sign(Algorithm.HMAC256(user.getId() + "9" + user.getGmtCreate()));
        return token;
    }

    /**
     * 从Http报文头中获取Token
     *
     * @param request
     * @return
     */
    public String getTokenByRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    /**
     * 通过http请求中的token获取user信息
     *
     * @param request
     * @return
     */
    public User getUserByRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = getUserIdByToken(token);
        if (userId != null) {
            return userService.findById(userId);
        } else {
            return null;
        }
    }

    /**
     * 通过http请求中的token获取sessionUser信息
     *
     * @param request
     * @return
     */
    public User getSessionUserByRequest(HttpServletRequest request) {
        return getSessionUserByToken(request.getHeader("Authorization"));
    }

    /**
     * 通过Token获取UserId
     *
     * @param token
     * @return
     */
    public Long getUserIdByToken(String token) {
        if (token == null) {
            return null;
        }
        String userId = JWT.decode(token).getAudience().get(0);
        if (userId == null) {
            return null;
        }
        return Long.parseLong(userId);
    }

    /**
     * 从Redis获取sessionUser
     *
     * @param token
     * @return
     */
    public User getSessionUserByToken(String token) {
        return (User) redisUtil.get(redisUtil.SESSION_USER + getUserIdByToken(token));
    }

    /**
     * Token处理：
     * ·生成Token；
     * ·Token存入Redis并设置过期时间；
     * ·sessionUser存入Redis并设置过期时间；
     * ·返回Token
     * 涉及该处理的场景：
     * ·login
     * ·register
     * ·github账户登录
     *
     * @param request
     * @param user
     * @return
     */
    public String tokenProcess(HttpServletRequest request, User user) {

        //generate new token
        String token = genToken(user);
        if (token == null) {
            throw new CustomizeException(CommonErrorCode.TOKEN_GENERATED_FAIL);
        }

        //存入Reids并设置过期时间
        redisUtil.set(redisUtil.TOKEN + token, token);
        redisUtil.expire(redisUtil.TOKEN + token, redisUtil.TOKEN_EXPIRE_TIME, TimeUnit.HOURS);
        System.out.println(token);

        //将用户信息存入Redis并设置过期时间，同Token
        redisUtil.set(redisUtil.SESSION_USER + user.getId(), user);
        redisUtil.expire(redisUtil.SESSION_USER + user.getId(), redisUtil.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        return token;
    }

    //从Redis中删除

    /**
     * 从Redis中删除Token和sessionUser
     *
     * @param request
     */
    public void delToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            //从Redis中删除Token
            redisUtil.del(redisUtil.TOKEN + token);
            Long userId = getUserIdByToken(token);
            //从Redis中删除sessionUser
            redisUtil.del(redisUtil.SESSION_USER + userId);
        }
    }

    /**
     * 判断Token是否存在于Redis中
     *
     * @param token
     * @return
     */
    public boolean existToken(String token) {
        if (token == null) {
            return false;
        }
        return redisUtil.hasKey(redisUtil.TOKEN + token);
    }

    public void authenticationProcess(String token) {

        if (token == null) {
            throw new CustomizeException(CommonErrorCode.TOKEN_NULL);
        }

        // 获取 token 中的 user id
        Long userId;
        try {
            userId = getUserIdByToken(token);
        } catch (JWTDecodeException j) {
            throw new CustomizeException(CommonErrorCode.TOKEN_INVALID);
        }

        User user = userService.findById(userId);
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
        if (!existToken(token)) {
            throw new CustomizeException(CommonErrorCode.TOKEN_HAS_EXPIRED);
        }

        // sessionUser是否已经失效
        User sessionUserByToken = this.getSessionUserByToken(token);
        if (sessionUserByToken == null || sessionUserByToken.getId().longValue() != user.getId().longValue()) {
            throw new CustomizeException(CommonErrorCode.TOKEN_SESSION_HAS_EXPIRED);
        }
    }

}
