package com.mg.community.dto;

import com.mg.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {

    private Long id;
    private String title;
    private String content;
    private String tag;
    private Long commentCount;
    private Long viewCount;
    private Long likeCount;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private User user;

}
