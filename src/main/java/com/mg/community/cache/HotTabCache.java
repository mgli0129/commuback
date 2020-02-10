package com.mg.community.cache;

import com.mg.community.dto.HotTabDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HotTabCache
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2019/12/20 16:35
 * @Version 1.0
 */
public class HotTabCache {

    public static List<HotTabDTO> getHotTopicTabs() {

        List<HotTabDTO> tabs = new ArrayList<>();
        HotTabDTO tab1 = new HotTabDTO();
        tab1.setIndex(1);
        tab1.setName("最多回复数");
        tabs.add(tab1);

        HotTabDTO tab2 = new HotTabDTO();
        tab2.setIndex(2);
        tab2.setName("最多阅读数");
        tabs.add(tab2);

        return tabs;
    }
}
