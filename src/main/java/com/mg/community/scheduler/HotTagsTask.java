package com.mg.community.scheduler;

import com.mg.community.cache.HotTopicsCache;
import com.mg.community.cache.HotTopicsDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName HotTagsTask
 * @Description 热门话题定时任务
 * @Author MGLi
 * @Date 2019/12/15 19:47
 * @Version 1.0
 */
@Component
public class HotTagsTask {

    @Autowired
    private HotTopicsCache hotTopicsCache;

    @Autowired
    private HotTopicsDataCache hotTopicsDataCache;

    /**
     * 热门话题的权值计算公式：
     * 权值 = 5 + 标签 + 问题回复数
     *
     * @每8小时刷新一次
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 8)
    public void genHotTopics() {
        hotTopicsCache.genHotTopics();
    }

    /**
     * 获取热门话题的统计信息;
     *
     * @每4个小时更新一次
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 4)
    public void getHotTopicData() {
        hotTopicsDataCache.genHotTopicData();
    }

}
