package com.mg.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mg.community.cache.HotTabCache;
import com.mg.community.cache.HotTopicsCache;
import com.mg.community.cache.HotTopicsDataCache;
import com.mg.community.common.OutputService;
import com.mg.community.dto.HotTopicDataDTO;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.dto.TagUserWallDTO;
import com.mg.community.model.Question;
import com.mg.community.model.User;
import com.mg.community.service.QuestionService;
import com.mg.community.service.UserService;
import com.mg.community.util.BaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName HotTopicsController
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2019/12/20 11:25
 * @Version 1.0
 */
@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class HotTopicsController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTopicsCache hotTopicsCache;

    @Autowired
    private HotTopicsDataCache hotTopicsDataCache;

    @Autowired
    private UserService userService;

    @Autowired
    private OutputService outputService;

    @GetMapping("/api/hottopics/{id}")
    public Object getHotTopics(@PathVariable(value = "id", required = false) int id,
                               @RequestParam(value = "tag", required = false) String tag,
                               @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize,
                               @RequestParam(value = "search", required = false) String search,
                               HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        List<Question> questions = new ArrayList<>();
        Question question = new Question();

        //使用“|”分割输入的多个搜索名字
        String searchStr = BaseUtil.seachItemOptimized(search);

        //pagehelper分页处理
        PageHelper.startPage(pageNum, pageSize);
        if (id == 1) {
            //最多回复数列表查询
            question.setTitle(searchStr);
            question.setTag(tag);
            questions = questionService.findAllByTagOrderByComment(question);
        } else if (id == 2) {
            //最多阅读数列表查询
            question.setTitle(searchStr);
            question.setTag(tag);
            questions = questionService.findAllByTagOrderByView(question);
        } else {
            //按时间倒序列表查询
            questions = questionService.findAllBySearch(searchStr);
        }

        PageInfo<Question> pageInfo = new PageInfo<Question>(questions);
        pageInfo.setList(null);
        //Add users into Questions
        List<QuestionDTO> questionDTOs = questionService.findAllDTO(questions);

        //获取热门话题统计信息
        if (StringUtils.isNotBlank(tag)) {
            HotTopicDataDTO hotTopicData = hotTopicsDataCache.getCurrentData(tag);
            if (hotTopicData != null) {
                outUni.put("hotTopicData", hotTopicData);
            }
            outUni.put("topic", tag);
        }

        //获取该话题的所有用户头像链接（图片墙）
        List<Question> list = questionService.findAllBySearch(null);
        List<TagUserWallDTO> tagUsers = list.stream().map(l -> {
            User user = userService.findById(l.getCreator());
            TagUserWallDTO tagUserWallDTO = new TagUserWallDTO();
            tagUserWallDTO.setId(user.getId());
            tagUserWallDTO.setAvatarUrl(user.getAvatarUrl());
            return tagUserWallDTO;
        }).collect(Collectors.toList());

        outUni.put("questions", questionDTOs);
        outUni.put("pageInfo", pageInfo);
        outUni.put("search", search);
        outUni.put("tabs", HotTabCache.getHotTopicTabs());
        outUni.put("id", id);
        outUni.put("tagUsers", tagUsers);
        outUni.put("common", outputService.getCommonOutput(request));

        return ResultDTO.okOf(outUni);
    }
}
