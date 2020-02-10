package com.mg.community.init;

import com.mg.community.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName InitApplication
 * @Description 本项目初始化
 * @Author MGLi
 * @Date 2020/2/7 23:51
 * @Version 1.0
 */
@Component
@Slf4j
public class InitApplication implements CommandLineRunner {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(String... args) throws Exception {
        //初始化Redis keys过期时间
        redisKeysExpired();
    }

    /**
     * 初始化Redis keys过期时间
     */
    public void redisKeysExpired() {
        redisUtil.expire(redisUtil.QUESTION, redisUtil.QUESTION_24H, TimeUnit.HOURS);
        redisUtil.expire(redisUtil.COMMENTS, redisUtil.QUESTION_24H, TimeUnit.HOURS);
        redisUtil.expire(redisUtil.QUESTION_RELATED, redisUtil.QUESTION_24H, TimeUnit.HOURS);
        log.info("set redis keys expire time............");
    }
}
