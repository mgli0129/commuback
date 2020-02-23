package com.mg.community.scheduler;

import com.mg.community.dto.QuestionRelatedDTO;
import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.Question;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName AutoSyncQuestionRelatedTask
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/23 11:43
 * @Version 1.0
 */
@Component
@Slf4j
public class AutoSyncQuestionRelatedTask {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private QuestionService questionService;

    /**
     * 自动更新Redis已存的问题相关内容；
     * 没fixedRate更新一次
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 20)
    public void autoSyncQuestionRelated() {
        log.info("Auto sync question related to redis --- start.............");
        if (redisUtil.testConnection()) {

            Map<String, Object> newRelateds = new HashMap<>();
            Map<Object, Object> qRelateds = redisUtil.hmget(redisUtil.QUESTION_RELATED);
            for (Map.Entry<Object, Object> entry : qRelateds.entrySet()) {
                Long id = (Long.parseLong((String) entry.getKey()));
                Question question = questionService.findById(id);
                List<Question> questions = questionService.findRelatedByTag(question);
                List<QuestionRelatedDTO> questionRelatedDTOS = questions.stream().map(q -> {
                    QuestionRelatedDTO questionRelatedDTO = new QuestionRelatedDTO();
                    BeanUtils.copyProperties(q, questionRelatedDTO);
                    return questionRelatedDTO;
                }).collect(Collectors.toList());
                newRelateds.put(id.toString(), questionRelatedDTOS);
            }
            if (newRelateds != null) {
                try {
                    redisUtil.hmset(redisUtil.QUESTION_RELATED, newRelateds);
                } catch (Exception e) {
                    throw new CustomizeException(CommonErrorCode.REDIS_OPERATION_ERROR);
                }
            }
        }
        log.info("Auto sync question related to redis --- end.............");
    }

}
