package com.yjy.examonline.domain.vo;

import com.yjy.examonline.domain.Template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 模板编辑展示时的回显结构
 */
public class TemplateFormVO {

    private static final String SPLIT_SEPARATOR = "\\}-\\|-\\{";

    private Long id;

    private String name;

    private String type;


    private Integer score1;
    /**
     * 静态模板，装载的都是单选题题号id    2}{1}{2}{3}
     * 动态模板，装载的都是单选题的数量    2{2}{3}{4}
     * 原question字符串中的第一个部分表示的分数 ,使用新的属性存放
     */
    private List<Integer> question1;

    private Integer score2;
    private List<Integer> question2;

    private Integer score3;
    private List<Integer> question3;

    private Integer score4;
    private List<Integer> question4;

    private Integer score5;
    private List<Integer> question5;

    private Integer totalScore;

    private String status;

    private Long tid;

    private String yl1;

    private String yl2;

    private String yl3;

    private String yl4;

    private Date createTime;

    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getScore1() {
        return score1;
    }

    public void setScore1(Integer score1) {
        this.score1 = score1;
    }

    public List<Integer> getQuestion1() {
        return question1;
    }

    public void setQuestion1(List<Integer> question1) {
        this.question1 = question1;
    }

    public Integer getScore2() {
        return score2;
    }

    public void setScore2(Integer score2) {
        this.score2 = score2;
    }

    public List<Integer> getQuestion2() {
        return question2;
    }

    public void setQuestion2(List<Integer> question2) {
        this.question2 = question2;
    }

    public Integer getScore3() {
        return score3;
    }

    public void setScore3(Integer score3) {
        this.score3 = score3;
    }

    public List<Integer> getQuestion3() {
        return question3;
    }

    public void setQuestion3(List<Integer> question3) {
        this.question3 = question3;
    }

    public Integer getScore4() {
        return score4;
    }

    public void setScore4(Integer score4) {
        this.score4 = score4;
    }

    public List<Integer> getQuestion4() {
        return question4;
    }

    public void setQuestion4(List<Integer> question4) {
        this.question4 = question4;
    }

    public Integer getScore5() {
        return score5;
    }

    public void setScore5(Integer score5) {
        this.score5 = score5;
    }

    public List<Integer> getQuestion5() {
        return question5;
    }

    public void setQuestion5(List<Integer> question5) {
        this.question5 = question5;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
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

    public TemplateFormVO(Template template) {
        this.id = template.getId();
        this.createTime = template.getCreateTime();
        this.updateTime = template.getUpdateTime();
        this.type = template.getType();
        this.name = template.getName();
        this.tid = template.getTid();
        this.yl1 = template.getYl1();
        this.yl2 = template.getYl2();
        this.yl3 = template.getYl3();
        this.yl4 = template.getYl4();
        this.totalScore = template.getTotalScore();
        this.status = template.getStatus();

        // 2}{1}{2}{3}
        {
            String questionStr = template.getQuestion1();
            String[] arrays = questionStr.split(SPLIT_SEPARATOR);
            this.setScore1(Integer.valueOf(arrays[0]));
            List<Integer> questions = new ArrayList<>();
            for (int i = 1; i < arrays.length; i++) {
                questions.add(Integer.valueOf(arrays[i]));
            }
            this.setQuestion1(questions);
        }

        {
            String questionStr = template.getQuestion2();
            String[] arrays = questionStr.split(SPLIT_SEPARATOR);
            this.setScore2(Integer.valueOf(arrays[0]));
            List<Integer> questions = new ArrayList<>();
            for (int i = 1; i < arrays.length; i++) {
                questions.add(Integer.valueOf(arrays[i]));
            }
            this.setQuestion2(questions);
        }

        {
            String questionStr = template.getQuestion3();
            String[] arrays = questionStr.split(SPLIT_SEPARATOR);
            this.setScore3(Integer.valueOf(arrays[0]));
            List<Integer> questions = new ArrayList<>();
            for (int i = 1; i < arrays.length; i++) {
                questions.add(Integer.valueOf(arrays[i]));
            }
            this.setQuestion3(questions);
        }

        {
            String questionStr = template.getQuestion4();
            String[] arrays = questionStr.split(SPLIT_SEPARATOR);
            this.setScore4(Integer.valueOf(arrays[0]));
            List<Integer> questions = new ArrayList<>();
            for (int i = 1; i < arrays.length; i++) {
                questions.add(Integer.valueOf(arrays[i]));
            }
            this.setQuestion4(questions);
        }

        {
            String questionStr = template.getQuestion5();
            String[] arrays = questionStr.split(SPLIT_SEPARATOR);
            this.setScore5(Integer.valueOf(arrays[0]));
            List<Integer> questions = new ArrayList<>();
            for (int i = 1; i < arrays.length; i++) {
                questions.add(Integer.valueOf(arrays[i]));
            }
            this.setQuestion5(questions);
        }

    }
}
