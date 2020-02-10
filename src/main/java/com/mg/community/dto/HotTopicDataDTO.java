package com.mg.community.dto;

import lombok.Data;

/**
 * @ClassName HotTopicDataDTO
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2019/12/20 20:05
 * @Version 1.0
 */
@Data
public class HotTopicDataDTO {

    private String tag;

    /**
     * 问题总数
     */
    private Long questionCount;

    /**
     * 评论总数
     */
    private Long commentCount;

    /**
     * 阅读总数
     */
    private Long viewCount;

}
