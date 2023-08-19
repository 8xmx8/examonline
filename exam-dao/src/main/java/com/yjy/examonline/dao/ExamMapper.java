package com.yjy.examonline.dao;

import com.yjy.examonline.domain.Exam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExamMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Exam record);

    int insertSelective(Exam record);

    Exam selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Exam record);

    int updateByPrimaryKey(Exam record);

    List<Exam> find(Map condition);

    List<Map> findByStudent(@Param("sid") Long sid, @Param("timeFlag") Integer timeFlag);

    void changeStatus(@Param("id") Long id, @Param("status") String status);

}