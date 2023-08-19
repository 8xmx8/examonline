package com.yjy.examonline.dao;

import com.yjy.examonline.domain.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKey(Question record);

    List<Question> findByTypeAndStatusAndCourse(@Param("type") String type, @Param("level") String level, @Param("course") String course);


}