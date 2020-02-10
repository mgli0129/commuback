package com.mg.community.dto;

import com.mg.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private String content;
    private Integer type;
    private Long likeCount;
    private Long commentCount;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private User user;
}
