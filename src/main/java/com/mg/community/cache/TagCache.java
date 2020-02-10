package com.mg.community.cache;

import com.mg.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {

    /**
     * 获取首页热门标签
     *
     * @return
     */
    public static List<TagDTO> getHotTags() {

        List<TagDTO> tagDTOs = new ArrayList<>();
        TagDTO front = new TagDTO();
        front.setCategoryName("前端");
        front.setTags(Arrays.asList("javascript","vue.js","css","html","html5","node.js","react.js","jquery","css3","es6","typescript","chrome","npm","bootstrap","Sass","Less","chrome-devtools","firefox","angular","coffeescript","safari","postcss","postman","fiddler","webkit","yarn","firebug","edge"));
        tagDTOs.add(front);

        TagDTO back = new TagDTO();
        back.setCategoryName("后端");
        back.setTags(Arrays.asList("php","java","python","c++","c","golang","spring","django","flask","springboot","c#","swoole","ruby","as",".net","ruby-on-rails","scala","rust","lavarel","爬虫"));
        tagDTOs.add(back);

        TagDTO mobile = new TagDTO();
        mobile.setCategoryName("移动端");
        mobile.setTags(Arrays.asList("java","android","ios","objective-c","小程序","swift","react-native","xcode","android-studio","webapp","flutter","kotlin"));
        tagDTOs.add(mobile);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql","redis","mongodb","sql","json","elasticsearch","nosql","memcached","postgresql","sqlite","mariadb"));
        tagDTOs.add(db);

        TagDTO other = new TagDTO();
        other.setCategoryName("其他");
        other.setTags(Arrays.asList("others"));
        tagDTOs.add(other);

        return tagDTOs;
    }

    public static String checkInvalid(String tags){
        if(StringUtils.isBlank(tags)){
           return null;
        }
        String[] inputTags = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = getHotTags();
        List<String> tagListTmp = tagDTOS.stream().flatMap(t -> t.getTags().stream()).collect(Collectors.toList());
        List<String> tagList = tagListTmp.stream().distinct().collect(Collectors.toList());
        String invalidTags = Arrays.stream(inputTags).filter(u -> !tagList.contains(u)).collect(Collectors.joining(","));
        return invalidTags;
    }

}