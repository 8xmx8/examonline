package com.yjy.examonline.dao;

import com.yjy.examonline.domain.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Teacher record);

    int insertSelective(Teacher record);

    Teacher selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Teacher record);

    int updateByPrimaryKey(Teacher record);

    Teacher findByName(String tname);

    int updatePwd(@Param("id") Long id, @Param("pass") String pass);

    long total(@Param("tname") String tname);

    List<Teacher> find(@Param("start") int start, @Param("length") int length, @Param("tname") String tname);

    List<Teacher> findAll();

    /**
     * 根据指定的老师id，找到哪些老师为该老师分享过模板
     *
     * @param id
     * @return
     */
    List<Teacher> findByShareFrom(Long id);

    /**
     * 根据指定的模板id，找到当前模板分享给了哪些老师
     *
     * @param templateId
     * @return
     */
    List<Teacher> findByShareTo(Long templateId);


}