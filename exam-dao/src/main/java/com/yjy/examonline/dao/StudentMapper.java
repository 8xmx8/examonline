package com.yjy.examonline.dao;

import com.yjy.examonline.domain.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StudentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    /**
     * 暂时使用map
     * 原因：没有班级相关的实体
     *
     * @param condition
     * @return 班级名称格式： 2021-软件-1班
     */
    List<Map> findClasses(Map condition);

    List<Student> findStudents(Map condition);

    void deleteByClasses(String classNames);

    void deletes(String ids);

    List<Student> findStudentsByClasses(Map condition);


    String findStudentIdsExcludeId(@Param("className") String className, @Param("id") Long id);

    List<Map> findClassesByNames(String classNames);

    List<Student> findExistStudent(List<Student> studentList);

    int classCountByName(String className);

    List<Map<String, Object>> findBindStudents(@Param("className") String className, @Param("array") String[] sidArray);

    List<Map<String, Object>> findUnbindStudents(@Param("className") String className, @Param("array") String[] sidBindArray);

    String findClassAllStudentIds(String className);

    List<Student> findByExam(Long examId);

    Student findByName(String sname);

    int updatePwd(@Param("id") Long id, @Param("pass") String pass);
}