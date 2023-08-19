package com.yjy.examonline.service;

import com.yjy.examonline.domain.Question;


public interface QuestionService {
    void save(Question question);

    Question findById(Long id);

    void update(Question question);
}
