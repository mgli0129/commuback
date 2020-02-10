package com.mg.community.controller;

import com.mg.community.cache.HotTabCache;
import com.mg.community.cache.TagCache;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.model.Question;
import com.mg.community.model.User;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/publish/{id}")
    public Object edit(@PathVariable(name = "id") Long id) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        QuestionDTO questionDTO=null;
        //先从Redis中获取，若不存在，再从数据库中读取
        if(redisUtil.testConnection()) {
            questionDTO = (QuestionDTO) redisUtil.hget(redisUtil.QUESTION, id.toString());
        }

        if(questionDTO == null){
            questionDTO = questionService.findDTOById(id);
            if(redisUtil.testConnection()) {
                redisUtil.hset(redisUtil.QUESTION, id.toString(), questionDTO);
            }
        }

        //页面回显字段信息
        outUni.put("title", questionDTO.getTitle());
        outUni.put("content", questionDTO.getContent());
        outUni.put("tag", questionDTO.getTag());
        outUni.put("tabs", HotTabCache.getHotTopicTabs());
        outUni.put("id", id);
        outUni.put("selectTags", TagCache.getHotTags());

        return ResultDTO.okOf(outUni);
    }

    @GetMapping("/publish")
    public Object publish() {
        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        outUni.put("selectTags", TagCache.getHotTags());
        return ResultDTO.okOf(outUni);
    }

    @PostMapping("/publish")
    public Object dopublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        //页面回显字段信息
        outUni.put("title", title);
        outUni.put("content", content);
        outUni.put("tag", tag);
        outUni.put("selectTags", TagCache.getHotTags());

        if (id != null) {
            outUni.put("id", id);
        }

        //校验页面字段
        if (title == null || title.equals("")) {
            return ResultDTO.errorOf(CustomizeErrorCode.PUBLISH_TITLE_EMPTY);
        }

        if (content == null || content.equals("")) {
            return ResultDTO.errorOf(CustomizeErrorCode.PUBLISH_CONTENT_EMPTY);
        }

        if (tag == null || tag.trim().equals("") || tag.trim().equals(",")) {
            return ResultDTO.errorOf(CustomizeErrorCode.PUBLISH_TAG_INVALID);
        }

        String checkInvalidTag = TagCache.checkInvalid(tag);
        if (!StringUtils.isBlank(checkInvalidTag)) {
            return ResultDTO.errorOf(CustomizeErrorCode.PUBLISH_TAG_INVALID, checkInvalidTag);
        }

        //处理tag多余的逗号
        String[] tagSplit = tag.split(",");
        String tagNew = Arrays.stream(tagSplit).map(t -> t.trim())
                .distinct()
                .filter(t -> t != null && t.length() > 0 && t!="")
                .collect(Collectors.joining(","));
        tag = tagNew;

        //写入数据库
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setTag(tag);
        User user = (User) request.getSession().getAttribute("user");
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);

        //更新Redis中question数据
        if(id !=null && redisUtil.testConnection()){
            QuestionDTO questionDTO = questionService.findDTOById(id);
            redisUtil.hset(redisUtil.QUESTION, id.toString(), questionDTO);

            //是否需要更新相关问题，此部分暂不考虑，因为相关问题一天更新一次也是可以的；
        }

        return ResultDTO.okOf(outUni);
    }

}
