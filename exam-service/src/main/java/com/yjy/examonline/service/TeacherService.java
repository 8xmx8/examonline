package com.yjy.examonline.service;

import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.domain.vo.PageVO;

import java.util.List;


public interface TeacherService {


    void save(Teacher teacher);

    Teacher findByName(String tname);

    void updatePwd(Long id, String pass);

    /**
     * 要求传入的page，rows有效
     *
     * @param page
     * @param rows
     * @param tname
     */
    PageVO find(int page, int rows, String tname);

    Teacher findById(Long id);

    void update(Teacher teacher);

    void deleteAll(String ids);

    int delete(Long id);

    /**
     * 批量保存，结果会成功和失败
     * 返回信息
     * 包括 导入总数据 ，成功数据 ，失败数据
     * 中间使用 | 隔开
     *
     * @param teachers
     * @return
     */
    String saves(List<Teacher> teachers);

    List<Teacher> findAll();

    /**
     * 找到为指定老师分享过模板的老师信息
     *
     * @param id
     * @return
     */
    List<Teacher> findByShare(Long id);
}
