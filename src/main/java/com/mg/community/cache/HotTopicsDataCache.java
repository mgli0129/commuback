package com.mg.community.cache;

import com.mg.community.dto.HotTopicDataDTO;
import com.mg.community.model.Question;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName HotTopicsData
 * @Description 统计热门话题的相关信息
 * @Author MGLi
 * @Date 2019/12/23 11:29
 * @Version 1.0
 */
@Component
@Data
public class HotTopicsDataCache {

    private List<HotTopicDataDTO> hotTopicDataDTOS = new ArrayList<>();

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTopicsCache hotTopicsCache;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取所有热门话题的统计信息
     */
    public void getDatas(){
        //先从Redis中获取
        if(redisUtil.testConnection()){
            if(redisUtil.hasKey(redisUtil.HOT_TOPIC_STATICS)){
                Map<Object, Object> hmget = redisUtil.hmget(redisUtil.HOT_TOPIC_STATICS);
                if(hmget != null){
                    //Redis中存在
                    System.out.println("从Redis 中获取hash");
                    hotTopicDataDTOS = (List) hmget.values().stream().collect(Collectors.toList());

                }else{
                    //从数据库中取
                    genHotTopicData();
                }
            }else{
                System.out.println("从数据库中获取数据");
                genHotTopicData();
            }
        }
    }

    /**
     * 从数据库中获取数据，并存入Redis
     */
    public void genHotTopicData(){
        if(hotTopicsCache.getHots() == null || hotTopicsCache.getHots().size() == 0){
            return;
        }
        Question question = new Question();
        hotTopicDataDTOS = hotTopicsCache.getHots().stream().map(h -> {
            question.setTag(h);
            return questionService.getHotTopicDatas(question);
        }).collect(Collectors.toList());

        //通过hash存入Redis
        if(redisUtil.testConnection()) {
            Map<String, Object> map = hotTopicDataDTOS.stream().collect(Collectors.toMap(HotTopicDataDTO::getTag, (p) -> p));
            redisUtil.del(redisUtil.HOT_TOPIC_STATICS);
            redisUtil.hmset(redisUtil.HOT_TOPIC_STATICS, map);
            redisUtil.expire(redisUtil.HOT_TOPIC_STATICS, 4l, TimeUnit.HOURS);
        }
    }

    /**
     * 获取当前访问的热门话题的统计信息
     * @param tag
     * @return
     */
    public HotTopicDataDTO getCurrentData(String tag){
        if(StringUtils.isBlank(tag)){
            return null;
        }
        //获取数据集
        getDatas();

        if(hotTopicDataDTOS != null) {
            for (HotTopicDataDTO data : hotTopicDataDTOS) {
                if (data.getTag().equals(tag)) {
                    return data;
                }
            }
        }
        return null;
    }

}
