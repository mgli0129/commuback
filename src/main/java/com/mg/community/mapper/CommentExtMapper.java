package com.mg.community.mapper;

import com.mg.community.model.Comment;
import com.mg.community.model.CommentExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentExtMapper {
    int incComment(Comment comment);
}
