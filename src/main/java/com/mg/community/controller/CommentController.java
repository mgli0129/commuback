package com.mg.community.controller;

import com.mg.community.dto.CommentDTO;
import com.mg.community.dto.CommentInputDTO;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.enums.CommentTypeEnum;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.model.Comment;
import com.mg.community.model.User;
import com.mg.community.service.CommentService;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 回复问题或者评论
     * @param commentInputDTO
     * @param request
     * @return
     */
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentInputDTO commentInputDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentInputDTO == null || StringUtils.isBlank(commentInputDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentInputDTO.getParentId());
        comment.setContent(commentInputDTO.getContent());
        comment.setType(commentInputDTO.getType());
        comment.setCommentator(user.getId());
        commentService.createOrUpdate(comment, user);

        //更新Redis中的comments数据
        if(redisUtil.testConnection()){
            QuestionDTO questionDTO = (QuestionDTO) redisUtil.hget(redisUtil.QUESTION, commentInputDTO.getParentId().toString());
            if(questionDTO != null){
                List<CommentDTO> comments = commentService.listByTargetId(questionDTO.getId(), CommentTypeEnum.QUESTION.getType());
                redisUtil.hset(redisUtil.COMMENTS, questionDTO.getId().toString(), comments);
            }
        }

        return ResultDTO.okOf();
    }

    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id") Long id) {
        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT.getType());
        outUni.put("comments", commentDTOS);
        outUni.put("toptitle", "【问题】---MG-COMMUNITY");

        return ResultDTO.okOf(outUni);
    }
}
