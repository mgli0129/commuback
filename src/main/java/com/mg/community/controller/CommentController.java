package com.mg.community.controller;

import com.mg.community.annotation.UserLoginToken;
import com.mg.community.common.OutputService;
import com.mg.community.dto.CommentDTO;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.enums.CommentTypeEnum;
import com.mg.community.exception.CommunityErrorCode;
import com.mg.community.model.Comment;
import com.mg.community.model.User;
import com.mg.community.service.AuthenticationService;
import com.mg.community.service.CommentService;
import com.mg.community.service.QuestionService;
import com.mg.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OutputService outputService;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * 回复问题或者评论
     * @param parentId
     * @param content
     * @param type
     * @param request
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/api/comment", method = RequestMethod.POST)
//    public Object post(@RequestBody CommentInputDTO commentInputDTO,
    public Object post(@RequestParam(value = "parentId") Long parentId,
                       @RequestParam(value = "content") String content,
                       @RequestParam(value = "type") int type,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            return ResultDTO.errorOf(CommunityErrorCode.TOKEN_SESSION_HAS_EXPIRED);
//        }

        if (StringUtils.isBlank(content)) {
            return ResultDTO.errorOf(CommunityErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(parentId);
        comment.setContent(content);
        comment.setType(type);
        comment.setCommentator(user.getId());
        commentService.createOrUpdate(comment, user);

        //更新Redis中的question和comments数据
        if(redisUtil.testConnection()){
            QuestionDTO dtoFromDB = questionService.findDTOById(parentId);
            QuestionDTO dtoFromRedis = (QuestionDTO) redisUtil.hget(redisUtil.QUESTION, parentId.toString());
            if(dtoFromRedis != null){
                List<CommentDTO> comments = commentService.listByTargetId(parentId, CommentTypeEnum.QUESTION.getType());
                //更新Redis上的数据
                redisUtil.hset(redisUtil.QUESTION, parentId.toString(), dtoFromDB);
                redisUtil.hset(redisUtil.COMMENTS, parentId.toString(), comments);
            }
        }

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }

    @RequestMapping(value = "/api/comment/{id}", method = RequestMethod.GET)
    public Object comments(@PathVariable("id") Long id) {
        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        CommentDTO commentDTO = commentService.findCommentDTOById(id);

        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT.getType());
        outUni.put("comments", commentDTOS);
        outUni.put("parentComment", commentDTO);

        return ResultDTO.okOf(outUni);
    }
}
