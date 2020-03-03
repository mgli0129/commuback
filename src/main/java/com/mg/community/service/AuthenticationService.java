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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @ClassName AuthenticationService
 * @Description Token处理服务类
 * 特别说明：
 * XXX-userId-token:   sessionId
 * session: setAttribute("userId", user);
 * 1）注册成功后生成token，将XXX-userId-token作为key，value为sessionId存入Redis，将token返回给前端；
 * 2）login时，验证token真伪，通过XXX-userId-token从Redsi中获取sessionId，均通过，则表示当前session有效；
 * 3）验证用户名和密码；
 * 4）定时任务：
 * ​    定时删除XXX-userId-token，每晚清理一次；
 * @Author MGLi
 * @Date 2020/2/19 14:02
 * @Version 1.0
 */
@Service
@Slf4j
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
        String token = this.getTokenByRequest(request);
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

        User sessionUser1 = (User) request.getSession().getAttribute(this.getUserIdByToken(this.getTokenByRequest(request)) + "");
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute(this.getUserIdByToken(this.getTokenByRequest(request)) + "");
        log.info("HttpServletRequest.getSession:=========================" + sessionUser1);
//        String token = this.getTokenByRequest(request);
//        String sessionId = this.getSessionIdByToken(token);
//        Object sessionUser = redisUtil.hget(redisUtil.SPRING_SESSION_SESSIONS + sessionId, redisUtil.SPRING_SESSION_ATTRIBUTE + this.getUserIdByToken(token));
        if (sessionUser == null) {
            return null;
        }
        log.info("HttpSession.getSession:=========================" + sessionUser);
        return sessionUser;
    }

    /**
     * 通过token获取sessionUser信息
     *
     * @param request
     * @return
     */
    public User getSessionUserByToken(HttpServletRequest request, String token) {

        if(StringUtils.isBlank(token)){
            return null;
        }
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute(this.getUserIdByToken(token) + "");
        if (sessionUser == null) {
            return null;
        }
        log.info("HttpSession.getSession:=========================" + sessionUser);
        return sessionUser;
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

        //将User放入session
        HttpSession session = request.getSession();
        session.setAttribute(user.getId() + "", user);

        //将sessionId存入Reids
        redisUtil.set(redisUtil.TOKEN + user.getId() + "-" + token, session.getId());
        System.out.println(token);

        return token;
    }

    //从Redis中删除

    /**
     * 从Redis中删除Token和sessionUser
     *
     * @param request
     */
    public void delToken(HttpServletRequest request) {
        String token = this.getTokenByRequest(request);
        if (token != null) {
            Long userIdByToken = this.getUserIdByToken(token);
            //从Redis中删除Token
            redisUtil.del(redisUtil.TOKEN + userIdByToken + "-" + token);
        }
    }

    /**
     * 判断Token是否存在于Redis中
     * 1）通过token查询Redis中是否存在？
     * 2）如果不存在，则表示已经过期；
     * 3）如果存在，取出sessionId，通过sessionId查询Redis中是否存在session？
     * 4）如果不存在，则表示已经过期，从Redis中删除token；
     * 5）如果存在，取出session里的user，并设置到当前的session里；
     *
     * @param token
     * @return
     */
    public boolean existToken(String token) {
        if (token == null) {
            return false;
        }
        if (!redisUtil.hasKey(redisUtil.TOKEN + getUserIdByToken(token) + "-" + token)) {
            return false;
        }
        String sessionId = (String) redisUtil.get(redisUtil.TOKEN + getUserIdByToken(token) + "-" + token);
        if (!redisUtil.hasKey(redisUtil.SPRING_SESSION_SESSIONS + sessionId)) {
            redisUtil.del(redisUtil.TOKEN + getUserIdByToken(token) + "-" + token);
            return false;
        }
//        User sessionUser = (User) redisUtil.hget(redisUtil.SPRING_SESSION_SESSIONS + sessionId, redisUtil.SPRING_SESSION_ATTRIBUTE + this.getUserIdByToken(token));
//        if (sessionUser == null) {
//            redisUtil.del(redisUtil.TOKEN + getUserIdByToken(token) + "-" + token);
//            return false;
//        }
        return true;
    }

    public void authenticationProcess(HttpServletRequest request, String token) {

        if (token == null) {
            throw new CustomizeException(CommonErrorCode.TOKEN_NULL);
        }

        // 获取 token 中的 user id
        Long userId;
        try {
            userId = this.getUserIdByToken(token);
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
    }

    private String getSessionIdByToken(String token) {
        if (token == null) {
            return null;
        }
        if (existToken(token)) {
            return (String) redisUtil.get(redisUtil.TOKEN + getUserIdByToken(token) + "-" + token);
        }
        return null;
    }

}
