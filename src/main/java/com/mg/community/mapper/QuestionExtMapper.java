package com.mg.community.mapper;

import com.mg.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    /**
     * 自动增加一次View
     * @param record
     * @return
     */
    int incView(Question record);

    int setNewView(Question record);

    int incComment(Question question);

    List<Question> selectRelatedByTag(Question question);
    List<Question> selectByOrTitle(Question question);
    List<Question> selectByCreatorOrSearch(Question question);

    List<Question> selectByTagWithSearchOByComment(Question question);

    List<Question> selectByTagWithSearchOByView(Question question);
}
