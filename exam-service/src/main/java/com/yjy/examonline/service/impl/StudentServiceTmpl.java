package com.yjy.examonline.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.dao.StudentMapper;
import com.yjy.examonline.domain.Student;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StudentServiceTmpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;


    @Override
    public void save(Student student) {
        //完善student
        student.setMnemonicCode(PinyinUtil.getPinyin(student.getSname(), ""));

        student.setPass(DigestUtil.md5Hex(student.getPass()));


        try {
            studentMapper.insert(student);
        } catch (DuplicateKeyException e) {
            log.debug("保存时，学号或名称重复 [{}],[{}]", student.getCode(), student.getSname());
            throw e;
        }
    }

    @Override
    public PageVO find(int pageNo, Map condition) {
        return this.findClasses(pageNo, condition);
    }

    public PageVO findClasses(int pageNo, Map condition) {
        PageHelper.startPage(pageNo, CommonData.DEFAULT_STU_CLASS_ROWS);
        List<Map> classes = studentMapper.findClasses(condition);
        PageInfo info = new PageInfo(classes);
        //将插件提供的PageInfo对象，组成我们之前更习惯使用的PageVO

        return new PageVO(
                info.getPageNum(),
                info.getPageSize(),
                info.getTotal(),
                info.getNavigateLastPage(),
                info.getStartRow(),
                info.getEndRow(),
                info.getList(),
                condition
        );

    }

    @Override
    public String saves(List<Student> students) {
        String msg = "";
        int count1 = 0;
        int count2 = 0;
        for (Student student : students) {
            try {
                this.save(student);
                count1++;
            } catch (DuplicateKeyException e) {
                count2++;
                msg += "【" + student.getCode() + "," + student.getSname() + "】因为学号或名称重复导致失败|";
            }
        }

        msg = "共导入记录【" + (count1 + count2) + "】条|" + "成功导入记录【" + count1 + "】条|" + "失败导入记录【" + count2 + "】条|" + msg;

        log.debug("msg {}", msg);

        return msg;
    }

    @Override
    public List<Student> findStudents(Map condition) {
        return studentMapper.findStudents(condition);
    }

    @Override
    public int delete(Long id) {
        return studentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Student findById(Long id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Student student) {
        student.setMnemonicCode(PinyinUtil.getPinyin(student.getSname(), ""));

        try {
            studentMapper.updateByPrimaryKeySelective(student);
        } catch (DuplicateKeyException e) {
            log.debug("修改时，学号或名称重复 [{}],[{}]", student.getCode(), student.getSname());
            throw e;
        }
    }

    @Override
    public void deleteByClasses(String classNames) {
        studentMapper.deleteByClasses(classNames);
    }

    @Override
    public void deleteStudents(String ids) {
        //${}原样拼串
        studentMapper.deletes(ids);
    }

    @Override
    public List<Student> findStudentsByClasses(Map condition) {
        return studentMapper.findStudentsByClasses(condition);
    }

    @Override
    public String findStudentIdsExcludeId(String className, Long studentId) {
        return studentMapper.findStudentIdsExcludeId(className, studentId);
    }

    @Override
    public List<Map> findClassesByNames(String classNames) {
        return studentMapper.findClassesByNames(classNames);
    }

    @Override
    public List<Student> findExistStudent(List<Student> studentList) {
        return studentMapper.findExistStudent(studentList);
    }

    @Override
    public boolean isExistClass(String className) {
        return studentMapper.classCountByName(className) > 0;
    }

    @Override
    public Student findByName(String sname) {
        return studentMapper.findByName(sname);
    }

    @Override
    public void updatePwd(Long id, String pass) {
        studentMapper.updatePwd(id, pass);
    }
}
