package com.mg.community.controller;

import com.mg.community.cache.TagCache;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.model.Question;
import com.mg.community.model.User;
import com.mg.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class AxiosController {

    @Autowired
    private QuestionService questionService;


    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/axios/question/{id}")
    public QuestionDTO axiosGetQuestion(@PathVariable("id") Long id, HttpServletResponse response) {
        QuestionDTO questionDTO = questionService.findDTOById(id);
        return questionDTO;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/axios/publish")
    public Object dopublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request,
            Model model) {

        System.out.println("coming from vue axios...................");

        //校验发布的字段
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

        return ResultDTO.okOf();
    }

}
