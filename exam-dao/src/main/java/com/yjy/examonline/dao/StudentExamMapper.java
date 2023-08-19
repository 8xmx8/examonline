package com.yjy.examonline.dao;

import com.yjy.examonline.domain.StudentExam;
import com.yjy.examonline.domain.dto.StudentExamDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StudentExamMapper {
    int deleteByPrimaryKey(@Param("examId") Long examId, @Param("studentId") Long studentId);

    int insert(StudentExam record);

    int insertSelective(StudentExam record);

    StudentExam selectByPrimaryKey(@Param("examId") Long examId, @Param("studentId") Long studentId);

    int updateByPrimaryKeySelective(StudentExam record);

    int updateByPrimaryKey(StudentExam record);

    List<Map> findRefClasses(Long examId);

    List<Long> findStudentIdByExam(Long examId);

    void removeReference(List<StudentExam> removeStudents);

    void addReference(List<StudentExam> addStudents);

    int findPagePathCount(Long examId);

    void updatePagePath(@Param("examId") Long examId, @Param("studentId") Long studentId, @Param("pagePath") String pagePath);

    void removePagePath(Long examId);

    void removeRefStudentsByExam(Long examId);

    StudentExam findStudentExamById(@Param("examId") Long examId, @Param("studentId") Long studentId);

    void changeStatus(@Param("examId") Long examId, @Param("studentId") Long studentId, @Param("status") String status);


    void updateStartTime(@Param("examId") Long examId, @Param("studentId") Long studentId);

    void updateAnswer(Map answerInfo);

    /**
     * 考试结束时，设置考试中的学生状态为 已完成
     *
     * @param examId
     */
    void updateFinishByExam(Long examId);

    /**
     * 考试结束时，设置未考试的学生状态为 缺考
     *
     * @param examId
     */
    void updateMissByExam(Long examId);

    List<Map> findClassesByExam(Long examId);


    List<StudentExamDTO> findStudentsByExamAndClass(@Param("examId") Long examId, @Param("className") String className);

    List<StudentExam> findInterruptStudents(Long examId);

    List<Integer> findStudentExamYears(Long studentId);

    List<StudentExamDTO> findStudentScores(@Param("studentId") Long studentId, @Param("year") Integer year);
}