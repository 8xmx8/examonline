package com.yjy.examonline.service.impl;

import com.yjy.examonline.dao.QuestionMapper;
import com.yjy.examonline.domain.Question;
import com.yjy.examonline.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceTmpl extends BaseLoggerTmpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;


    @Override
    public void save(Question question) {
        log.debug("即将保存的题目为：[{}]", question);
        questionMapper.insert(question);
    }

    @Override
    public Question findById(Long id) {
        return questionMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Question question) {
        questionMapper.updateByPrimaryKeySelective(question);
    }
}
