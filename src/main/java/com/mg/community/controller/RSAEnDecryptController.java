package com.mg.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mg.community.annotation.UserLoginToken;
import com.mg.community.dto.NotificationDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.model.User;
import com.mg.community.service.RSAEnDecryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RSAEnDecryptController
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/20 23:30
 * @Version 1.0
 */
@Controller
@RestController
public class RSAEnDecryptController {

    @Autowired
    private RSAEnDecryptService rsaEnDecryptService;

    @GetMapping("/api/rsapublickey")
    public Object getRSAPublicKey(HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        String publicKey = rsaEnDecryptService.getPublicKey();
        outUni.put("publickey", publicKey);
        return ResultDTO.okOf(outUni);
    }

}
