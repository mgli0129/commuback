package com.mg.community.util;

/**
 * @ClassName RedisConst
 * @Description Redis 常量定义
 * @Author MGLi
 * @Date 2020/2/6 12:32
 * @Version 1.0
 */
public interface RedisConst {

    /***********************************************************************************/
    /*
     * key相关
     */
    //热门话题-key
    public final static String HOT_TOPIC = "hot_topic";

    //热门话题-统计信息-key
    public final static String HOT_TOPIC_STATICS = "hot_topic_statics";

    //问题-key
    public final static String QUESTION = "question";

    //评论-key
    public final static String COMMENTS = "comments";

    //相关问题-key
    public final static String QUESTION_RELATED = "question_related";

    //阅读数-key
    public final static String QUESTION_VIEW_COUNT = "question_view_count-";

    /***********************************************************************************/
    /*
     * time相关
     */
    //热门话题-key-8h
    public final static Long HOT_TOPIC_8H = 8L;
    //热门话题-统计信息-4h
    public final static Long HOT_TOPIC_STATICS_4H = 4L;
    //问题-key-24h
    public final static Long QUESTION_24H = 24L;
    //评论-key-24h
    public final static Long COMMENTS_24H = 24L;
    //相关问题-key-24h
    public final static Long QUESTION_RELATED_24H = 24L;
    //阅读数-20h（需要在question的自动删除时间内及时更新至数据库）
    public final static Long QUESTION_VIEW_COUNT_20H = 20L;

    /***********************************************************************************/


}
