package com.mg.community.scheduler;

import com.mg.community.model.Question;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName ViewCountUpdTask
 * @Description 定时将Redis中的question view count更新到数据库
 * @Author MGLi
 * @Date 2020/2/7 17:49
 * @Version 1.0
 */
@Component
@Slf4j
public class ViewCountUpdTask {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private QuestionService questionService;

    /**
     * 自动将Redis的阅读数更新至数据库；
     * 没fixedRate更新一次
     */
    @Scheduled(fixedRate = 1000*60*60*20)
    public void autoSyncViewCount() {
        log.info("Auto sync view count to database --- start.............");
        if(redisUtil.testConnection()){
            Set keys = redisUtil.keys(redisUtil.QUESTION_VIEW_COUNT + "*");
            if(keys != null){
                Iterator it = keys.iterator();
                while(it.hasNext()){
                    String key = (String) it.next();
                    long value = ((Integer) redisUtil.get(key)).longValue();
                    String id = key.split("-")[1];
                    Question question = questionService.findById(Long.parseLong(id));
                    question.setViewCount(value);
                    questionService.setNewView(question);
                }
            }
        }
        log.info("Auto sync view count to database --- end.............");
    }

}
