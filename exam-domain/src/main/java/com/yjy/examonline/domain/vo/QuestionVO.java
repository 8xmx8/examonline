package com.yjy.examonline.domain.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 添加静态模板时，对加入的试题回显信息的装载
 * 未来学生考试后，老师批阅考卷时，需要先显示考卷（考题）
 * 也会使用当前VO
 * 那个时候需要记录考题所得分数
 */
public class QuestionVO implements Serializable {
    /**
     * 页面显示的题号从1开始
     */
    private int index;

    private String type;

    private String subject;

    private List<String> optionList;

    private List<String> answerList;

    private int score;

    /**
     * 批阅考卷时的最终得分，来自tstudent_exam.review字段，拆分而得
     */
    private int endScore;

    /**
     * 批阅考卷时记录学生的自定义答案，以供显示
     */
    private List<String> endAnswerList;

    /**
     * 批阅考卷时记录考题的批阅信息（针对于填空题和综合题）暂定方案
     */
    private String review = "无";

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public List<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<String> answerList) {
        this.answerList = answerList;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getEndScore() {
        return endScore;
    }

    public void setEndScore(int endScore) {
        this.endScore = endScore;
    }

    public List<String> getEndAnswerList() {
        return endAnswerList;
    }

    public void setEndAnswerList(List<String> endAnswerList) {
        this.endAnswerList = endAnswerList;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public QuestionVO(int index, String type, String subject, List<String> optionList, List<String> answerList) {
        this.index = index;
        this.type = type;
        this.subject = subject;
        this.optionList = optionList;
        this.answerList = answerList;
    }

    public QuestionVO(int index, String type, String subject, List<String> optionList, List<String> answerList, int score) {
        this.index = index;
        this.type = type;
        this.subject = subject;
        this.optionList = optionList;
        this.answerList = answerList;
        this.score = score;
    }

    public QuestionVO() {
    }
}
