package com.mg.community.util;

import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CommunityErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName EnvInfo
 * @Description 获取当前ip和端口
 * @Author MGLi
 * @Date 2020/1/21 21:23
 * @Version 1.0
 */
@Component
@Slf4j
public class EnvInfo {

    @Autowired
    private Environment environment;

    private String port;
    private String ip;
    private InetAddress localHost;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${file.publicIp}")
    private String publicIp;

    public String getUrl() {

        port = environment.getProperty("local.server.port");
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.debug("Get environment error: {}" + ResultDTO.errorOf(CommonErrorCode.SYSTEM_GET_ENV_ERROR));
        }
        if (profile.equals("prd")) {
            return "http://" + publicIp + ":" + port + "/";
        } else {
            ip = localHost.getHostAddress();
            return "http://" + ip + ":" + port + "/";
        }
    }

}
