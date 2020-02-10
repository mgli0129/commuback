package com.mg.community.service.impl;

import com.mg.community.dto.CommentDTO;
import com.mg.community.enums.CommentTypeEnum;
import com.mg.community.enums.NotificationTypeEnum;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.mapper.CommentExtMapper;
import com.mg.community.mapper.CommentMapper;
import com.mg.community.model.*;
import com.mg.community.service.CommentService;
import com.mg.community.service.NotificationService;
import com.mg.community.service.QuestionService;
import com.mg.community.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service("CommentService")
public class CommentServiceImpl implements CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    @Autowired(required = false)
    private CommentExtMapper commentExtMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void createOrUpdate(Comment comment, User commentator) {
        Comment parentComment = null;
        Question question = null;

        //校验
        if (comment.getParentId() == null || comment.getParentId() == 0L) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_NOT_FOUND);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复comment
            parentComment = findById(comment.getParentId());
            if (parentComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
        } else {
            //回复question
            question = questionService.findById(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }

        //插入数据或修改数据
        if (comment.getId() == null) {
            //Insert
            comment.setGmtCreate(System.currentTimeMillis());
            comment.setGmtModified(System.currentTimeMillis());
            comment.setLikeCount(0L);
            comment.setCommentCount(0L);
            commentMapper.insert(comment);

            //给问题的评论增加1
            if (comment.getType() == CommentTypeEnum.QUESTION.getType()) {
                question.setCommentCount(1L);
                questionService.incComment(question);

                //通知提问题的人
                createNotify(question.getCreator(), comment, NotificationTypeEnum.REPLY_QUESTION, commentator.getName(), question.getTitle(), question.getId());
            } else {
                //给评论的评论增加1
                parentComment.setCommentCount(1L);
                commentExtMapper.incComment(parentComment);

                //获取一级回复的父类id问题
                question = questionService.findById(parentComment.getParentId());

                //通知提原回复的人
                createNotify(parentComment.getCommentator(), comment, NotificationTypeEnum.REPLY_COMMENT, commentator.getName(), question.getTitle(), question.getId());
            }

        } else {
            //Update
            comment.setGmtModified(System.currentTimeMillis());
            CommentExample commentExample = new CommentExample();
            commentExample.createCriteria().andIdEqualTo(comment.getId());
            commentMapper.updateByExampleSelective(comment, commentExample);
        }
    }

    private Comment findById(Long id) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andIdEqualTo(id);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments == null){
            return null;
        }
        return comments.get(0);
    }

    private void createNotify(Long receiver, Comment comment, NotificationTypeEnum type, String commentator, String commentTitle, Long questionId) {
        //自己回复自己的，不添加通知
        if (receiver.equals(comment.getCommentator())) {
            return;
        }

        Notification notification = new Notification();
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setType(type.getType());
        notification.setOuterid(comment.getParentId());
        notification.setNotifierName(commentator);
        notification.setNotifyTitle(commentTitle);
        notification.setQuestionid(questionId);
        notificationService.create(notification);
    }

    @Override
    public List<CommentDTO> listByTargetId(Long id, Integer type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type);
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        userIds.addAll(commentators);

        //获取评论人并转为map
        List<User> users = userService.listByIds(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment to commentDTO
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOs;
    }
}
