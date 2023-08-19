package com.yjy.examonline.controller;

import com.yjy.examonline.common.BaseController;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.domain.Exam;
import com.yjy.examonline.domain.Student;
import com.yjy.examonline.domain.StudentExam;
import com.yjy.examonline.domain.vo.QuestionVO;
import com.yjy.examonline.domain.vo.StudentExamVO;
import com.yjy.examonline.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/exam")

public class ExamController extends BaseController {

    @Autowired
    private ExamService examService;

    @RequestMapping("/exam.html")
    public String toExam(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loginStudent");
        //获得当前这个学生的考试信息（默认当日）
        List<Map> exams = examService.findByStudent(student.getId(), 1);
        model.addAttribute("exams", exams);
        return "exam/exam";
    }

    @RequestMapping("/examGrid.html")
    public String toExamGrid(HttpSession session, Integer timeFlag, Model model) {
        Student student = (Student) session.getAttribute("loginStudent");
        List<Map> exams = examService.findByStudent(student.getId(), timeFlag);
        model.addAttribute("exams", exams);
        return "exam/exam::#examGrid";
    }

    @RequestMapping("/checkEnter")
    @ResponseBody
    public int checkEnter(Long examId, HttpSession session) {
        Student student = (Student) session.getAttribute("loginStudent");

        Exam exam = examService.findById(examId);
        StudentExam studentExam = examService.findStudentExamById(student.getId(), examId);

        //先判断是否超时
        //如果没有超时，正常返回
        //如果已经超时，需要更新时间和状态
        if (studentExam.getStatus().equals("考试中")) {
            //需要判断时间
            if (exam.getStartTime() != null) {
                //考试区间，检查当前系统时间 是否超过endTime
                Date curr = new Date();
                Date endTime = exam.getEndTime();
                if (curr.after(endTime)) {
                    //当前时间已经超过了考试结束时间
                    studentExam.setStatus("已完成");
                    studentExam.setEndTime(endTime);
                    //更新考试考试状态
                    examService.update(studentExam);
                }
            } else {
                //设置了时长
                Integer duration = exam.getDuration();
                Date startTime = studentExam.getStartTime();
                Date curr = new Date();
                long l = curr.getTime() - startTime.getTime();
                if (l / 1000 / 60 > duration) {
                    //超出时长了
                    studentExam.setStatus("已完成");
                    l = startTime.getTime() + duration * 60 * 1000;
                    Date endTime = new Date(l);
                    studentExam.setEndTime(endTime);
                    //更新考试考试状态
                    examService.update(studentExam);
                }
            }
        }


        if (exam.getStatus().equals("丢弃")) {
            //不常出现，学生在看到考试上瞬间，教师端就丢弃了考试。
            return 9;
        }


        if (studentExam.getStatus().equals("已完成")) {
            //考试可能未结束，考生已经完成考试
            return 8;
        }


        if (exam.getStatus().equals("已完成")) {
            //考试结束
            return 7;
        }


        if (exam.getStartTime() != null) {
            //设置了考试区间
            if (exam.getStartTime().after(new Date())) {
                //还没有到考试时间
                return 6;
            }
        }

        //可以正常进入考试
        if (!studentExam.getStatus().equals("考试中")) {
            //同时改变学生的考试状态（考试中）
            //如果是第一个进入考试的同学，考试的状态也需要变为考试中-正在进行
            examService.startExam(student.getId(), examId);
        }
        return 1;

    }

    @RequestMapping("/page.html")
    public String toPage(Long examId, HttpSession session, Model model) {
        //需要考试信息
        Exam exam = examService.findById(examId);

        //需要考生信息
        Student student = (Student) session.getAttribute("loginStudent");

        //需要试卷信息,读取文件，拆解文件内容，组成试题集合
        StudentExam studentExam = examService.findStudentExamById(student.getId(), examId);
        String pagePath = studentExam.getPagePath();
        pagePath = CommonData.PAGE_ROOT_PATH + pagePath;
        List<QuestionVO> questions = this.readPage(pagePath);

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);

        //studentExam中存有我们需要的答案信息，但需要处理后在thymeleaf中才可以使用
        //studentExam-->StudentExamVO--> (answer1,2,3,4,5->List)

        StudentExamVO vo = new StudentExamVO(studentExam);
        model.addAttribute("studentExamVO", vo);

        //还需要更新一下学生考试开始时间
        if (studentExam.getStartTime() == null) {
            examService.updateStartTime(student.getId(), examId);
            studentExam.setStartTime(new Date());
        }

        return "exam/page";
    }

    private List<QuestionVO> readPage(String pagePath) {
        List<QuestionVO> questions = new ArrayList<>();
        try {

            FileReader reader = new FileReader(pagePath);
            BufferedReader r = new BufferedReader(reader);
            StringBuilder content = new StringBuilder();
            char[] cs = new char[0x100];
            int length;
            while ((length = r.read(cs)) != -1) {
                content.append(new String(cs, 0, length));
            }

            log.debug("page info : \r\n {}", content);

            String[] array = content.toString().split(CommonData.QUESTION_OPTION_SEPARATOR);

            log.debug("page array : \r\n {}", Arrays.toString(array));

            int index = 0;
            int no = 1;//题号
            QuestionVO question = null;
            for (String value : array) {
                if (index == 0) {
                    //一道新的考题
                    question = new QuestionVO();
                    question.setIndex(no++);
                    question.setType(value);
                    questions.add(question);
                } else if (index == 1) {
                    question.setScore(Integer.parseInt(value));
                } else if (index == 2) {
                    question.setSubject(value);
                } else if (index == 3) {
                    question.setOptionList(Arrays.asList(value.split(CommonData.SPLIT_SEPARATOR)));
                } else if (index == 4) {
                    //value是答案
                    question.setAnswerList(Arrays.asList(value.split(CommonData.SPLIT_SEPARATOR)));
                } else if (index == 5) {
                    //分隔符，当前这道题操作完毕
                    index = 0;
                    continue;
                }
                index++;
            }
            r.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return questions;
    }

    @RequestMapping("/updateAnswer")
    @ResponseBody
    public void updateAnswer(@RequestParam Map answerInfo) {
        examService.updateAnswer(answerInfo);
    }

    @RequestMapping("/submitPage")
    @ResponseBody
    public void submitPage(@RequestParam Map answerInfo) {
        examService.submitPage(answerInfo);
    }
}


