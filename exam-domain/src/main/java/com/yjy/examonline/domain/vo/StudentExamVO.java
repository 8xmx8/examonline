package com.yjy.examonline.domain.vo;

import com.yjy.examonline.domain.StudentExam;

import java.util.ArrayList;
import java.util.List;

public class StudentExamVO {

    private static final String SPLIT_SEPARATOR = "\\}-\\|-\\{";

    private StudentExam studentExam;

    /**
     * List{
     * 单选题=1
     * 单选题=-1
     * 多选题=-1
     * 多选题=List{1,2,3}
     * 判断题=-1
     * 判断题=0
     * 填空题=List{"",""}
     * 综合题="aa"
     * 综合题=""
     * }
     */
    List<Object> answerList;

    public StudentExamVO(StudentExam studentExam) {
        this.studentExam = studentExam;
        this.answerList = new ArrayList<>();

        if (
                (studentExam.getAnswer1() == null || "".equals(studentExam.getAnswer1()))
                        && (studentExam.getAnswer2() == null || "".equals(studentExam.getAnswer2()))
                        && (studentExam.getAnswer3() == null || "".equals(studentExam.getAnswer3()))
                        && (studentExam.getAnswer4() == null || "".equals(studentExam.getAnswer4()))
                        && (studentExam.getAnswer5() == null || "".equals(studentExam.getAnswer5()))
        ) {
            //首次进入考试页面，还没有任何答案记录
            return;
        }
        //单选题处理
        if (studentExam.getAnswer1() != null && !"".equals(studentExam.getAnswer1())) {
            String answerStr = studentExam.getAnswer1();
            String[] array = answerStr.split(SPLIT_SEPARATOR);
            for (String an : array) {
                if ("no".equals(an)) {
                    answerList.add(-1);
                } else {
                    answerList.add(Integer.valueOf(an));
                }
            }
        }

        //多选题处理
        if (studentExam.getAnswer2() != null && !"".equals(studentExam.getAnswer2())) {
            //{"1,2,3","no","1,3"}  3道多选，第一题BCD,第二题没有答案，第三题BD
            String answerStr = studentExam.getAnswer2();
            String[] array = answerStr.split(SPLIT_SEPARATOR);
            for (String anStr : array) {
                if ("no".equals(anStr)) {
                    answerList.add(-1);
                } else {
                    //此题有答案，可能是"1,2,3"
                    List<Integer> childAnwserList = new ArrayList<>();
                    String[] array1 = anStr.split(",");
                    for (String an : array1) {
                        childAnwserList.add(Integer.valueOf(an));
                    }
                    answerList.add(childAnwserList);
                }
            }
        }

        //判断题题处理
        if (studentExam.getAnswer3() != null && !"".equals(studentExam.getAnswer3())) {
            //{"1,2,3","no","1,3"}  3道多选，第一题BCD,第二题没有答案，第三题BD
            String answerStr = studentExam.getAnswer3();
            String[] array = answerStr.split(SPLIT_SEPARATOR);
            for (String anStr : array) {
                if ("no".equals(anStr)) {
                    answerList.add(-1);
                } else {
                    answerList.add(Integer.valueOf(anStr));
                }
            }
        }


        //填空题处理
        if (studentExam.getAnswer4() != null && !"".equals(studentExam.getAnswer4())) {
            //{"1,2,3","no,no","1,3"}  3道多选，第一题BCD,第二题没有答案，第三题BD
            String answerStr = studentExam.getAnswer4();
            String[] array = answerStr.split(SPLIT_SEPARATOR);
            for (String anStr : array) {
                List<String> childAnwserList = new ArrayList<>();
                String[] array1 = anStr.split(",");
                for (String an : array1) {
                    if ("no".equals(an)) {
                        //当前空没有答案
                        childAnwserList.add("");
                    } else {
                        childAnwserList.add(an);
                    }
                }
                answerList.add(childAnwserList);
            }
        }

        //综合题处理
        if (studentExam.getAnswer5() != null && !"".equals(studentExam.getAnswer5())) {
            String answerStr = studentExam.getAnswer5();
            String[] array = answerStr.split(SPLIT_SEPARATOR);
            for (String an : array) {
                if ("no".equals(an)) {
                    answerList.add("");
                } else {
                    answerList.add(an);
                }
            }
        }


    }

    public StudentExamVO() {
    }

    public StudentExam getStudentExam() {
        return studentExam;
    }

    public void setStudentExam(StudentExam studentExam) {
        this.studentExam = studentExam;
    }

    public List<Object> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Object> answerList) {
        this.answerList = answerList;
    }
}
