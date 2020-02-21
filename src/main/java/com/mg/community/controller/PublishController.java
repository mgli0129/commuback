package com.mg.community.controller;

import com.mg.community.annotation.UserLoginToken;
import com.mg.community.cache.HotTabCache;
import com.mg.community.cache.TagCache;
import com.mg.community.common.OutputService;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CommunityErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.Question;
import com.mg.community.model.User;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OutputService outputService;

    @UserLoginToken
    @GetMapping("/api/publish/{id}")
    public Object edit(@PathVariable(name = "id") Long id,
                       HttpServletRequest request) {

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

        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }

    @UserLoginToken
    @GetMapping("/api/publish")
    public Object publish(HttpServletRequest request) {
        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        outUni.put("selectTags", TagCache.getHotTags());
        outUni.put("common", outputService.getCommonOutput(request));
        return ResultDTO.okOf(outUni);
    }

    @UserLoginToken
    @PostMapping("/api/publish")
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

        if (id != null && id > 0) {
            outUni.put("id", id);
        }

        //校验页面字段
        if (title == null || title.equals("")) {
            throw new CustomizeException(CommunityErrorCode.PUBLISH_TITLE_EMPTY);
        }

        if (content == null || content.equals("")) {
            throw new CustomizeException(CommunityErrorCode.PUBLISH_CONTENT_EMPTY);
        }

        if (tag == null || tag.trim().equals("") || tag.trim().equals(",")) {
            throw new CustomizeException(CommunityErrorCode.PUBLISH_TAG_INVALID);
        }

        String checkInvalidTag = TagCache.checkInvalid(tag);
        if (!StringUtils.isBlank(checkInvalidTag)) {
            Map<String, String> map = new HashMap<>();
            map.put("&1",checkInvalidTag);
            throw new CustomizeException(CommunityErrorCode.PUBLISH_TAG_INVALID, map);
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
        if (id != null && id > 0) {
            question.setId(id);
        }
        questionService.createOrUpdate(question);

        //更新Redis中question数据
        if(id !=null && redisUtil.testConnection()){
            QuestionDTO questionDTO = questionService.findDTOById(id);
            redisUtil.hset(redisUtil.QUESTION, id.toString(), questionDTO);

            //是否需要更新相关问题，此部分暂不考虑，因为相关问题一天更新一次也是可以的；
        }

        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }

}
