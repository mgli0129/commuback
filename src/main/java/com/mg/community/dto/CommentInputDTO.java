package com.mg.community.dto;

import lombok.Data;

@Data
public class CommentInputDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
