package com.yjy.examonline.test;

import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.service.TeacherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 初始化教室信息
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class Test1 {
    @Autowired
    private TeacherService teacherService;

    @Test
    public void t1() {
        Teacher t = new Teacher();
        t.setTname("张三2453");
        t.setPass("333");
        teacherService.save(t);
    }
}
