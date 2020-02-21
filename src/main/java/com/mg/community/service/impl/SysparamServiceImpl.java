package com.mg.community.service.impl;

import com.mg.community.mapper.SysparamMapper;
import com.mg.community.model.Sysparam;
import com.mg.community.model.SysparamExample;
import com.mg.community.service.SysparamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SysparamServiceImpl
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/8 12:37
 * @Version 1.0
 */
@Service("SysparamService")
public class SysparamServiceImpl implements SysparamService {

    @Autowired(required = false)
    private SysparamMapper sysparamMapper;

    @Override
    public String findRSAPublicKey() {
        return findSysParam().getPublicKey();
    }

    @Override
    public String findRSAPrivateKey() {
        return findSysParam().getPrivateKey();
    }

    @Override
    public Sysparam findSysParam() {
        SysparamExample sysparamExample = new SysparamExample();
        sysparamExample.createCriteria();
        List<Sysparam> sysparams = sysparamMapper.selectByExample(sysparamExample);
        if(sysparams.size() == 0){
            return null;
        }
        return sysparams.get(0);
    }

    @Override
//    @Cacheable
    public boolean connectRedis() {
        SysparamExample sysparamExample = new SysparamExample();
        sysparamExample.createCriteria();
        List<Sysparam> sysparams = sysparamMapper.selectByExample(sysparamExample);
        if(sysparams.size() == 0){
            return false;
        }
        if(sysparams.get(0).getRedis().equals("Y")){
            return true;
        }
        return false;
    }

    @Override
    public void updatePublicKey(String key) {
        Sysparam sysparam = new Sysparam();
        sysparam.setId(1);
        sysparam.setPublicKey(key);
        updateSysParam(sysparam);
    }

    @Override
    public void updatePrivateKey(String key) {
        Sysparam sysparam = new Sysparam();
        sysparam.setId(1);
        sysparam.setPrivateKey(key);
        updateSysParam(sysparam);
    }

    @Override
    public void updateSysParam(Sysparam sysparam) {
        SysparamExample sysparamExample = new SysparamExample();
        sysparamExample.createCriteria().andIdEqualTo(1);
        sysparamMapper.updateByExampleSelective(sysparam, sysparamExample);
    }
}
