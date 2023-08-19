package com.yjy.examonline.service;

import com.yjy.examonline.domain.Student;
import com.yjy.examonline.domain.vo.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public interface StudentService {

    /**
     * 批量保存
     * 允许部分成功，部分失败
     *
     * @param students
     * @return 保存结果
     * @see "com.duyi.examonline.service.TeacherService.saves()"
     */
    String saves(List<Student> students);

    Logger log = LoggerFactory.getLogger(StudentService.class);

    PageVO findClasses(int pageNo, Map condition);

    void save(Student student);

    void update(Student student);

    /**
     * 分页过滤查询，可能有3个条件
     * 第几届，专业，班级
     *
     * @param pageNo
     * @param condition
     */
    PageVO find(int pageNo, Map condition);

    /**
     * @param condition code sname className 三者之一
     * @return
     */
    List<Student> findStudents(Map condition);

    /**
     * 精确删除，主键 id
     *
     * @param id
     * @return
     */
    int delete(Long id);

    Student findById(Long id);

    /**
     * 可以删除多个班级的学生信息
     *
     * @param classNames 多个班级使用逗号隔开
     *                   格式： 2020-软件-1班,2020-软件-2班,
     */
    void deleteByClasses(String classNames);

    /**
     * 1,2,3,4,5
     *
     * @param ids
     */
    void deleteStudents(String ids);

    List<Student> findStudentsByClasses(Map condition);

    /**
     * 获得某一个班级中，排除某一个学生后的，其他所有学生的id组合
     * 考试信息模块使用的功能
     */
    String findStudentIdsExcludeId(String className, Long studentId);

    /**
     * 根据一组班级名称的字符串，获得其中所有班级的信息（班级名-className + 总人数-total）
     *
     * @param classNames
     * @return
     */
    List<Map> findClassesByNames(String classNames);

    List<Student> findExistStudent(List<Student> studentList);

    boolean isExistClass(String className);

    Student findByName(String sname);

    void updatePwd(Long id, String pass);
}
