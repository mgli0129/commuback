package com.mg.community.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mg.community.CommunityApplicationTests;
import com.mg.community.model.Question;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @ClassName UserServiceTest
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/23 21:00
 * @Version 1.0
 */
public class UserServiceTest extends CommunityApplicationTests {


    @Autowired
    private QuestionService questionService;

    @Test
    public void pageHelperTest() {
        int offSet = 0;
        int limit = 5;
        List<Question> questions = new ArrayList<>();

        //得到标签和权值的无序的Map
        /*
           1) 没数据；ok
           2）有数据，没到5倍数临界点；ok
           3）有数据，到5倍数临界点；ok
         */
        while (offSet == 0 || questions.size() == 5) {
            PageHelper.startPage(offSet, limit);
            questions = questionService.findAllBySearch(null);
            PageInfo<Question> pageInfo = new PageInfo<Question>(questions);
            int pages = pageInfo.getPages();
            if (offSet == pages - 1) {
                break;
            }
            System.out.println(questions.size());
            offSet++;
        }
    }
}
