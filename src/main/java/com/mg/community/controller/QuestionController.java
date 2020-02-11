package com.mg.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mg.community.dto.CommentDTO;
import com.mg.community.dto.NotificationDTO;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.enums.CommentTypeEnum;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.exception.CustomizeException;
import com.mg.community.model.Question;
import com.mg.community.model.User;
import com.mg.community.service.CommentService;
import com.mg.community.service.NotificationService;
import com.mg.community.service.QuestionService;
import com.mg.community.util.BaseUtil;
import com.mg.community.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RestController
public class QuestionController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/question/{id}")
    public Object question(@PathVariable("id") Long id) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        QuestionDTO questionDTO=null;
        List<CommentDTO> comments=null;
        List<QuestionDTO> questionRelated = null;
        Long viewCount = 0L;

        if(redisUtil.testConnection()){
            questionDTO = (QuestionDTO) redisUtil.hget(redisUtil.QUESTION, id.toString());
            if(questionDTO != null){
                comments = (List<CommentDTO>) redisUtil.hget(redisUtil.COMMENTS, id.toString());
                questionRelated = (List<QuestionDTO>) redisUtil.hget(redisUtil.QUESTION_RELATED, id.toString());
                log.info("Get question from Redis.........................");
            }
        }
        if(questionDTO == null){
            //从数据库中获取数据
            questionDTO = questionService.findDTOById(id);
            if(questionDTO == null){
                //Redis和数据库均没有该数据，跑出错误
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION.getType());
            questionRelated = questionService.findRelatedByTag(questionDTO);
            log.info("Get question from Database.........................");

            //存入Redis
            if(redisUtil.testConnection()){
                redisUtil.hset(redisUtil.QUESTION, id.toString(), questionDTO);
                redisUtil.hset(redisUtil.COMMENTS, id.toString(), comments);
                redisUtil.hset(redisUtil.QUESTION_RELATED, id.toString(), questionRelated);
                //设置过期时间
//                redisUtil.expire(redisUtil.QUESTION, redisUtil.QUESTION_24H, TimeUnit.HOURS);
//                redisUtil.expire(redisUtil.COMMENTS, redisUtil.QUESTION_24H, TimeUnit.HOURS);
//                redisUtil.expire(redisUtil.QUESTION_RELATED, redisUtil.QUESTION_24H, TimeUnit.HOURS);
            }
        }

        //点击一次Question将增加一个View
        viewCount = questionDTO.getViewCount();
        if(redisUtil.testConnection()){
            //更新viewCount
            if(!redisUtil.hasKey(redisUtil.QUESTION_VIEW_COUNT+id.toString())){
                redisUtil.set(redisUtil.QUESTION_VIEW_COUNT+id.toString(), viewCount);
                redisUtil.expire(redisUtil.QUESTION_VIEW_COUNT, redisUtil.QUESTION_VIEW_COUNT_20H, TimeUnit.HOURS);
            }
            redisUtil.incr(redisUtil.QUESTION_VIEW_COUNT+id.toString(),1L);
            viewCount = ((Integer) redisUtil.get(redisUtil.QUESTION_VIEW_COUNT+id.toString())).longValue();
        }else{
            questionService.incView(questionService.findById(id));
            viewCount++;
        }
        questionDTO.setViewCount(viewCount);

        outUni.put("question", questionDTO);
        outUni.put("comments", comments);
        outUni.put("questionRelated", questionRelated);
        outUni.put("toptitle", "【问题】---MG-COMMUNITY");

        return ResultDTO.okOf(outUni);
    }

    @GetMapping("/question/publish")
    public Object myQuestion(@RequestParam(value = "pageNum", required = false, defaultValue="1") int pageNum,
                             @RequestParam(required = false, defaultValue="8") int pageSize,
                             @RequestParam(value = "search",required = false) String search,
                             HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        //使用“|”分割输入的多个搜索名字
        String searchStr = BaseUtil.seachItemOptimized(search);

        User user = (User) request.getSession().getAttribute("user");
        //pagehelper分页处理
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionService.findQuestionByCreatorOrSearch(user.getId(),searchStr);
        PageInfo<Question> pageInfo = new PageInfo<Question>(questions);
        pageInfo.setList(null);

        outUni.put("questions", questions);
        outUni.put("pageInfo", pageInfo);
        outUni.put("search", search);

        return ResultDTO.okOf(outUni);
    }

    @GetMapping("/question/reply")
    public Object yourLatelyReply(@RequestParam(value = "pageNum", required = false, defaultValue="1") int pageNum,
                           @RequestParam(required = false, defaultValue="8") int pageSize,
                           HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        User user = (User) request.getSession().getAttribute("user");
        //pagehelper分页处理
        PageHelper.startPage(pageNum, pageSize);
        List<NotificationDTO> notifications = notificationService.findNotificationByReceiver(user.getId());
        PageInfo<NotificationDTO> pageInfo = new PageInfo<NotificationDTO>(notifications);
        pageInfo.setList(null);

        outUni.put("notifications", notifications);
        outUni.put("pageInfo", pageInfo);

        return ResultDTO.okOf(outUni);
    }

}
