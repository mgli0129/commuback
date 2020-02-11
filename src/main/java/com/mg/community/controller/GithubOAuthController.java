package com.mg.community.controller;

import com.mg.community.Provider.GithubProvider;
import com.mg.community.dto.AccessTokenDTO;
import com.mg.community.dto.GithubUser;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.model.User;
import com.mg.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class GithubOAuthController {

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserService userService;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @GetMapping("/callback")
    public Object callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpServletResponse response) {
        if (code == null || code.equals("")){
            return ResultDTO.errorOf(CustomizeErrorCode.GITHUB_ACCOUNT_VERIFIED_FAILURE);
        }
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(githubClientId);
        accessTokenDTO.setClient_secret(githubClientSecret);
        accessTokenDTO.setRedirect_uri(githubRedirectUri);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        System.out.println(accessToken);

        String accessCode = accessToken.split("&")[0].split("=")[1];
        accessTokenDTO.setCode(accessCode);
        GithubUser githubUser = githubProvider.getGithubUser(accessTokenDTO);

        if (githubUser != null) {
            User userAcc = userService.findByAccountId(githubUser.getLogin());
            String token = null;
            if(userAcc == null){
                //登录成功，写入数据库
                User user = new User();
                user.setAccountId(githubUser.getLogin());
                user.setName(githubUser.getName());
                token = UUID.randomUUID().toString();
                user.setToken(token);
                user.setAvatarUrl(githubUser.getAvatarUrl());
                userService.createOrUpdate(user);
            }else{
                token = userAcc.getToken();
            }
            //写入cookie
            Cookie cookie = new Cookie("token", token);
            response.addCookie(cookie);
            //输出格式测试
            Map<String, Object> outUni = new HashMap<String, Object>();
            outUni.put("token", token);

            return ResultDTO.okOf(outUni);
        } else {
            //登录失败
            return ResultDTO.errorOf(CustomizeErrorCode.GITHUB_ACCOUNT_NOT_EXIST);
        }
    }

    @GetMapping("/logout")
    public Object logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResultDTO.okOf();
    }

}
