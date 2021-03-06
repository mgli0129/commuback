package com.mg.community.cache;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mg.community.dto.PriorityDTO;
import com.mg.community.model.Question;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName HotTopicsCache
 * @Description 热门话题数据处理
 * @Author MGLi
 * @Date 2019/12/16 13:57
 * @Version 1.0
 */

@Component
@Data
@Slf4j
public class HotTopicsCache {

    private List<String> hots = new ArrayList<>();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private QuestionService questionService;

    /**
     * 小顶堆排序
     *
     * @param sources
     */
    public void sortPriorites(Map<String, Long> sources) {
        int max = 5;
        PriorityQueue<PriorityDTO> priorityDTOS = new PriorityQueue<>(max);

        for (String s : sources.keySet()) {

            if (s != null) {
                PriorityDTO priorityDTO = new PriorityDTO();
                priorityDTO.setTag(s);
                priorityDTO.setPriority((Long) sources.get(s));
                if (priorityDTOS.size() < max) {
                    priorityDTOS.add(priorityDTO);
                } else {
                    PriorityDTO minPriority = priorityDTOS.peek();
                    if (priorityDTO.compareTo(minPriority) > 0) {
                        priorityDTOS.poll();
                        priorityDTOS.add(priorityDTO);
                    }
                }
            }
        }
        List<String> sortedPros = new ArrayList<>();
        PriorityDTO p = priorityDTOS.poll();
        while (p != null) {
            //使用List的index为0，可以不断插入进去，可以做到先进后出，也就是将小顶堆的转化为大顶List
            sortedPros.add(0, p.getTag());
            p = priorityDTOS.poll();
        }

        /*
            增加redis操作
         */
        //存入redis数据库
        redisUtil.del(RedisUtil.HOT_TOPIC);
        redisUtil.lSet(RedisUtil.HOT_TOPIC, sortedPros);
        //保存在本地内存
        hots = sortedPros;
    }

    public List<String> getHots() {

        //优先返回本地内存
        if (!hots.isEmpty()) {
            return hots;
        }
        //从Redis中取
        if (!redisUtil.hasKey(redisUtil.HOT_TOPIC)) {
            //手动获取一次数据
            genHotTopics();
        }
        log.info("hottopic got from redis......");
        return (List) redisUtil.lGet(RedisUtil.HOT_TOPIC, 0, -1).get(0);

    }

    public void genHotTopics() {

        int offSet = 1;
        int limit = 5;
        List<Question> questions = new ArrayList<>();
        Map<String, Long> priorities = new HashMap<>();
        List<String> outPut = new ArrayList<>();

        log.info("getHotTopics start: {}", new Date());

        //得到标签和权值的无序的Map
        /*
         ***使用PageHelper后，在数据量刚好是5的倍数时，出现死循环***
         * 原因是最后一次执行PageHelper.startPage(offSet, limit)后，question已经没有数据了，再次执行startPage时，
         * 会自动的保留最后一次的数据，满足有5条数据，因此，出现死循环；
         */
        //总页数
        int pages = 0;
        PageInfo<Question> pageInfo = null;
        while (offSet == 1 || questions.size() == 5) {
            PageHelper.startPage(offSet, limit);
            questions = questionService.findAllBySearch(null);
            for (Question question : questions) {
                String[] tags = StringUtils.split(question.getTag(), ",");
                for (String tag : tags) {
                    Long priority = priorities.get(tag);
                    if (priority != null) {
                        priorities.put(tag, priority + 5 + question.getCommentCount());
                    } else {
                        priorities.put(tag, 5 + question.getCommentCount());
                    }
                }
            }

            //只统计一次
            if (offSet == 1) {
                pageInfo = new PageInfo<Question>(questions);
                pages = pageInfo.getPages();
            }
            for (Question question : questions) {
                log.info("Current ID: " + question.getId());
            }
            log.info("offSet=" + offSet + "-------pages=" + pages);
            //如果在最后一次翻页获取数据后不退出，startPage会保留最后一次的记录，无限循环;
            //如果没有数据，也退出循环
            if (offSet == pages || pages == 0) {
                break;
            }
            offSet++;
        }

        //选出前X位排名
        sortPriorites(priorities);

        log.info("getHotTopics stop: {}", new Date());
    }

}
