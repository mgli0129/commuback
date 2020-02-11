package com.mg.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mg.community.cache.HotTopicsCache;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.model.Question;
import com.mg.community.service.QuestionService;
import com.mg.community.util.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@Slf4j
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTopicsCache hotTopicsCache;

    @GetMapping("/")
    public Object index(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                        @RequestParam(required = false, defaultValue = "15") int pageSize,
                        @RequestParam(value = "search", required = false) String search) {
        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();

        //使用“|”分割输入的多个搜索名字
        String searchStr = BaseUtil.seachItemOptimized(search);

        //pagehelper分页处理
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionService.findAllBySearch(searchStr);
        PageInfo<Question> pageInfo = new PageInfo<Question>(questions);
        pageInfo.setList(null);

        //Add users into Questions
        List<QuestionDTO> questionDTOs = questionService.findAllDTO(questions);

        //获取热门话题
        List<String> hotTopics = hotTopicsCache.getHots();

        outUni.put("questions", questionDTOs);
        outUni.put("pageInfo", pageInfo);
        outUni.put("search", search);
        outUni.put("hotTopics", hotTopics);

        return ResultDTO.okOf(outUni);
    }

}
