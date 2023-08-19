package com.yjy.examonline.controller;

import com.yjy.examonline.domain.Student;
import com.yjy.examonline.domain.dto.StudentExamDTO;
import com.yjy.examonline.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.List;

@RequestMapping("/score")
@Controller
public class ScoreController {
    @Autowired
    private ExamService examService;

    @RequestMapping("/score.html")
    public String toScore(HttpSession session, Model model){
        Student student = (Student) session.getAttribute("loginStudent");
        Long studentId = student.getId() ;
        //先查询获得当前学生拥有考试的年份
        List<Integer> years= examService.findStudentExamYears(studentId);
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        if(years.size() == 0 || years.get(0) != currYear){
            //当前这一年来查看成绩，但没有当前这一年的成绩
            years.add(0,currYear);
        }
        model.addAttribute("years",years);

        //还需要查询当前年份考试信息
        List<StudentExamDTO> scores = examService.findStudentScores(studentId,years.get(0));
        model.addAttribute("scores",scores) ;

        return "score/score" ;
    }

    @RequestMapping("/scoreGrid.html")
    public String toScoreGrid(Integer year,HttpSession session,Model model){
        Student student = (Student) session.getAttribute("loginStudent");
        Long studentId = student.getId() ;
        List<StudentExamDTO> scores = examService.findStudentScores(studentId,year);
        model.addAttribute("scores",scores) ;

        return "score/score::#scoreGrid" ;
    }

}
