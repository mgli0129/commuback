package com.mg.community.service;

import com.mg.community.model.Sysparam;

/**
 * @ClassName SysparamService
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/8 12:35
 * @Version 1.0
 */
public interface SysparamService {

    /**
     * 获取RSA公钥
     * @return
     */
    public String findRSAPublicKey();

    /**
     * 获取RSA私钥
     * @return
     */
    public String findRSAPrivateKey();

    public Sysparam findSysParam();

    /**
     * 是否连接Redis
     * @return
     */
    public boolean connectRedis();

    void updatePublicKey(String key);

    void updatePrivateKey(String key);

    void updateSysParam(Sysparam sysparam);
}
