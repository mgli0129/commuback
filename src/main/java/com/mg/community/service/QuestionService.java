package com.mg.community.service;

import com.mg.community.dto.HotTopicDataDTO;
import com.mg.community.dto.QuestionDTO;
import com.mg.community.model.Question;

import java.util.List;

public interface QuestionService {

    /**
     * 供pagehelper使用
     * @return
     */
    List<Question> findAllBySearch(String search);

    /**
     * 通过标签和查找项查询所有问题清单，且按回复数进行排序；
     *
     * @param question
     * @return
     */
    List<Question> findAllByTagOrderByComment(Question question);

    /**
     * 通过标签和查找项查询所有问题清单，且按阅读数进行排序；
     *
     * @param question
     * @return
     */
    List<Question> findAllByTagOrderByView(Question question);

    /**
     * 补全当前页的question结果集
     * @param questions
     * @return
     */
    List<QuestionDTO> findAllDTO(List<Question> questions);

    List<Question> findQuestionByCreatorOrSearch(Long creator, String search);

    QuestionDTO findDTOById(Long id);

    Question findById(Long id);

    void createOrUpdate(Question question);

    void incView(Question question);

    void setNewView(Question question);

    void incComment(Question comment);

    List<QuestionDTO> findRelatedByTag(QuestionDTO questionDTO);

    HotTopicDataDTO getHotTopicDatas(Question question);
}
