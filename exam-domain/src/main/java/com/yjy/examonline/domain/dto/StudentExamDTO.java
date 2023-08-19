package com.yjy.examonline.domain.dto;

import java.util.Date;

/**
 * 试卷模块中，关于学生考试信息传输对象
 */
public class StudentExamDTO {

    private Long examId;

    private Long studentId;

    private String examGroup;

    private String status;

    private Date startTime;

    private Date endTime;

    private String answer1; //1}-|-{2}-|-{3

    private String answer2;

    private String answer3;

    private String answer4;

    private String answer5;

    private String review4;

    private String review5;

    private Integer score;

    private String pagePath;

    private String yl1;

    private String yl2;

    private String yl3;

    private String yl4;

    private Date createTime;

    private Date updateTime;


    private String code;//学号

    private String sname;//学生姓名

    private String ename;//考试名称


    public StudentExamDTO(Long examId, Long studentId, String examGroup, String status, Date startTime, Date endTime, String answer1, String answer2, String answer3, String answer4, String answer5, String review4, String review5, Integer score, String pagePath, String yl1, String yl2, String yl3, String yl4, Date createTime, Date updateTime, String code, String sname) {
        this.examId = examId;
        this.studentId = studentId;
        this.examGroup = examGroup;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.answer5 = answer5;
        this.review4 = review4;
        this.review5 = review5;
        this.score = score;
        this.pagePath = pagePath;
        this.yl1 = yl1;
        this.yl2 = yl2;
        this.yl3 = yl3;
        this.yl4 = yl4;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.code = code;
        this.sname = sname;
    }

    public StudentExamDTO() {
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getExamGroup() {
        return examGroup;
    }

    public void setExamGroup(String examGroup) {
        this.examGroup = examGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getAnswer5() {
        return answer5;
    }

    public void setAnswer5(String answer5) {
        this.answer5 = answer5;
    }

    public String getReview4() {
        return review4;
    }

    public void setReview4(String review4) {
        this.review4 = review4;
    }

    public String getReview5() {
        return review5;
    }

    public void setReview5(String review5) {
        this.review5 = review5;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getYl1() {
        return yl1;
    }

    public void setYl1(String yl1) {
        this.yl1 = yl1;
    }

    public String getYl2() {
        return yl2;
    }

    public void setYl2(String yl2) {
        this.yl2 = yl2;
    }

    public String getYl3() {
        return yl3;
    }

    public void setYl3(String yl3) {
        this.yl3 = yl3;
    }

    public String getYl4() {
        return yl4;
    }

    public void setYl4(String yl4) {
        this.yl4 = yl4;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    /**
     * 为examGroup设置了一个别名，使用者可以通过examGroup和className获得相同的信息
     */
    public String getClassName() {
        return examGroup;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }
}
