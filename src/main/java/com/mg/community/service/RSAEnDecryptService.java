package com.mg.community.service;

import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.util.Base64Util;
import com.mg.community.util.RSAUtil;
import com.mg.community.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

/**
 * @ClassName RSAEnDecryptService
 * @Description RSA 加解密服务
 * @Author MGLi
 * @Date 2020/2/20 21:53
 * @Version 1.0
 */
@Service
@Slf4j
public class RSAEnDecryptService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysparamService sysparamService;

    /**
     * 获取RSA公钥
     *
     * @return
     */
    public String getPublicKey() {

        String privateKey = null;
        String publicKey = null;

        //先从Redis读取
        try {
            privateKey = (String) redisUtil.get(RedisUtil.RSA_PRIVATE_KEY);
            publicKey = (String) redisUtil.get(RedisUtil.RSA_PUBLIC_KEY);
            //Redis不存在
            if (StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {

                //从数据库读取
                publicKey = sysparamService.findRSAPublicKey();
                privateKey = sysparamService.findRSAPrivateKey();
                if (StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {
                    //生成一对新的密钥
                    //系统初始化后，第一个注册的用户会生成，后续如果因为系统原因导致没数据，再生产会出大事，
                    //因此最好是单独保管第一次生成的密钥对；
                    KeyPair keyPair = RSAUtil.getKeyPair();
                    privateKey = new String(Base64Util.encoder(keyPair.getPrivate().getEncoded()));
                    publicKey = new String(Base64Util.encoder(keyPair.getPublic().getEncoded()));
                    //存入数据库
                    sysparamService.updatePublicKey(publicKey);
                    sysparamService.updatePrivateKey(privateKey);
                }
                //存入Redis
                redisUtil.set(RedisUtil.RSA_PRIVATE_KEY, privateKey);
                redisUtil.set(RedisUtil.RSA_PUBLIC_KEY, publicKey);
            }
            return publicKey;
        } catch (Exception e) {
            throw new CustomizeException(CommonErrorCode.SYSTEM_RSA_ERROR);
        }
    }

    /**
     * 获取RSA私钥
     *
     * @return
     */
    public String getPrivateKey() {
        String privateKey = null;
        privateKey = (String) redisUtil.get(RedisUtil.RSA_PRIVATE_KEY);
        //Redis不存在
        if (StringUtils.isEmpty(privateKey)) {
            //从数据库读取
            privateKey = sysparamService.findRSAPrivateKey();
            if (StringUtils.isEmpty(privateKey)) {
                throw new CustomizeException(CommonErrorCode.SYSTEM_RSA_ERROR);
            }
            redisUtil.set(RedisUtil.RSA_PRIVATE_KEY, privateKey);
        }
        return privateKey;
    }
}

