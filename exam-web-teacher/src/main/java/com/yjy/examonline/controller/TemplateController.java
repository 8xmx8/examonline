package com.yjy.examonline.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.domain.Question;
import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.domain.Template;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.domain.vo.QuestionVO;
import com.yjy.examonline.domain.vo.TemplateFormVO;
import com.yjy.examonline.service.DictionaryService;
import com.yjy.examonline.service.QuestionService;
import com.yjy.examonline.service.TeacherService;
import com.yjy.examonline.service.TemplateService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.yjy.examonline.common.CommonData.DEFAULT_QUESTION_SESSION;

@Controller
@RequestMapping("/template")
public class TemplateController extends BaseControllerAndUpload {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private QuestionService questionService;


    @Autowired
    private TeacherService teacherService;


    @RequestMapping("/add.html")
    public String toAdd(Model model, HttpSession session) {
        //现在因为完成了静态模板的试题添加（缓存）
        //所以在没有保存和取消之前，每次访问这个静态模板页时
        //需要将之前缓存的试题信息一同展示在网页上
        String questionKey = CommonData.DEFAULT_QUESTION_SESSION;
        List<Question> questionCache = getSessionValue(session, questionKey);
        if (questionCache == null) {
            //首次访问模板页面，还没有缓存，构建一个
            questionCache = new ArrayList<>();
            session.setAttribute(questionKey, questionCache);
        }
        //将question 转换成 questionVO
        List<QuestionVO> questions = new ArrayList<>();
        int index = 1;
        for (Question question : questionCache) {
            QuestionVO questionVO = questionCast(question, index++);
            questions.add(questionVO);
        }
        model.addAttribute("questions", questions);


        //查询，并携带课程信息
        List<String> courses = dictionaryService.findCourses();
        model.addAttribute("courses", courses);
        return "template/add";
    }


    @RequestMapping("/dynamic/save")
    @ResponseBody
    public boolean dynamicSave(@RequestParam Map<String, String> info, HttpSession session) {
        log.debug("info [{}]", info);

        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);

        Template template = new Template();
        template.setType(info.get("type"));
        template.setName(info.get("name"));
        template.setTotalScore(Integer.parseInt(info.get("totalScore")));
        template.setYl1(info.get("course"));
        template.setStatus(CommonData.DEFAULT_TEMPLATE_STATUS);
        template.setTid(teacher.getId());

        //单选题处理
        String score1 = info.get("score1");
        String count11 = info.get("count11");
        String count12 = info.get("count12");
        String count13 = info.get("count13");
        String question1 = score1 + CommonData.SEPARATOR + count11 + CommonData.SEPARATOR + count12 + CommonData.SEPARATOR + count13;
        template.setQuestion1(question1);

        //多选题处理
        String score2 = info.get("score2");
        String count21 = info.get("count21");
        String count22 = info.get("count22");
        String count23 = info.get("count23");
        String question2 = score2 + CommonData.SEPARATOR + count21 + CommonData.SEPARATOR + count22 + CommonData.SEPARATOR + count23;
        template.setQuestion2(question2);

        //判断题处理
        String score3 = info.get("score3");
        String count31 = info.get("count31");
        String count32 = info.get("count32");
        String count33 = info.get("count33");
        String question3 = score3 + CommonData.SEPARATOR + count31 + CommonData.SEPARATOR + count32 + CommonData.SEPARATOR + count33;
        template.setQuestion3(question3);

        //填空题处理
        String score4 = info.get("score4");
        String count41 = info.get("count41");
        String count42 = info.get("count42");
        String count43 = info.get("count43");
        String question4 = score4 + CommonData.SEPARATOR + count41 + CommonData.SEPARATOR + count42 + CommonData.SEPARATOR + count43;
        template.setQuestion4(question4);

        //综合题处理
        String score5 = info.get("score5");
        String count51 = info.get("count51");
        String count52 = info.get("count52");
        String count53 = info.get("count53");
        String question5 = score5 + CommonData.SEPARATOR + count51 + CommonData.SEPARATOR + count52 + CommonData.SEPARATOR + count53;
        template.setQuestion5(question5);

        try {
            templateService.save(template);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }

    }

    @RequestMapping("/questionTemplate.html")
    public String toQuestionTemplate() {
        return "template/questionTemplate";
    }

    /**
     * 注意：缓存试题信息，并返回试题信息展示的图示模板
     *
     * @param question
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/cacheQuestion")
    public String cacheQuestion(@RequestParam(name = "index", defaultValue = "0") int index, String id, Question question, HttpSession session, Model model) {
        String cacheKey = DEFAULT_QUESTION_SESSION;
        if (id != null && !"".equals(id)) {
            //编辑
            cacheKey += id;
        }
        question.setId(null);
        if (index == 0) {
            //添加考题
            //question缺少 course，status，tid
            question.setStatus(CommonData.DEFAULT_QUESTION_STATUS);
            Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
            question.setTid(teacher.getId());
        }

        //将这个question装入缓存
        //人为规定缓存就是session
        //人为规定session.key = questionCache
        List<Question> questionCache = getSessionValue(session, cacheKey);
        if (questionCache == null) {
            questionCache = new ArrayList<>();
            session.setAttribute(cacheKey, questionCache);
        }
        if (index == 0) {
            //添加考题，直接将其追加到缓存末尾
            questionCache.add(question);
        } else {
            //编辑考题，修改考题信息
            Question old_question = questionCache.get(index - 1);
            old_question.setLevel(question.getLevel());
            old_question.setSubject(question.getSubject());
            old_question.setOptions(question.getOptions());
            old_question.setAnswer(question.getAnswer());

            question = old_question;
        }

        //此时缓存完毕。需要回显。回显时需要组成VO对象
        index = index == 0 ? questionCache.size() : index;
        QuestionVO questionVO = questionCast(question, index);

        //因为在toAdd方法中，每次访问模板页面，都需要携带之前缓存的试题信息，默认展示
        //从而网页模板中，需要对集合进行处理
        //此次单一试题缓存，利用的也是同一个模板
        //为了实现公用性，也需要将一个试题组成集合
        List<QuestionVO> questions = new ArrayList<>();
        questions.add(questionVO);
        model.addAttribute("questions", questions);

        return "template/questionViewTemplate::questionView";
    }

    @RequestMapping("/removeQuestion")
    @ResponseBody
    public void removeQuestion(int index, String id, HttpSession session) {
        String cacheKey = "questionCache";
        if (id != null && !"".equals(id)) {
            //编辑
            cacheKey += id;
        }
        List<Question> questionCache = getSessionValue(session, cacheKey);
        questionCache.remove(index - 1);
    }


    @RequestMapping("/removeQuestions")
    @ResponseBody
    public void removeQuestions(String indexes, String id, HttpSession session) {
        String cacheKey = "questionCache";
        if (id != null && !"".equals(id)) {
            //编辑
            cacheKey += id;
        }
        List<Question> questionCache = getSessionValue(session, cacheKey);
        String[] indexArray = indexes.split(",");
        //因为ArrayList集合内部，删除某一个位置的元素后，后面的元素，会向前移动
        //从而接下来要删除的元素的位置就发生了变化
        //所以我们考虑从后向前删除  1,2,5
        //Arrays.sort(indexArray);
        for (int i = indexArray.length - 1; i >= 0; i--) {
            int index = Integer.parseInt(indexArray[i]);
            questionCache.remove(index - 1);
        }
    }

    @RequestMapping("/editQuestion")
    @ResponseBody
    public QuestionVO editQuestion(int index, HttpSession session) {
        List<Question> questionCache = getSessionValue(session, DEFAULT_QUESTION_SESSION);
        Question question = questionCache.get(index - 1);
        return questionCast(question, index);
    }

    private QuestionVO questionCast(Question question, int index) {
        QuestionVO questionVO = new QuestionVO();
        //原来cache中有10条记录，当前这个就是第11个，序号也应该是11，恰好是cache.size()
        questionVO.setIndex(index);
        questionVO.setSubject(question.getSubject());
        questionVO.setType(question.getType());

        String[] optionArray = question.getOptions().split(CommonData.SPLIT_SEPARATOR);
        questionVO.setOptionList(Arrays.asList(optionArray));

        String[] answerArray = question.getAnswer().split(CommonData.SPLIT_SEPARATOR);
        questionVO.setAnswerList(Arrays.asList(answerArray));

        return questionVO;
    }

    @RequestMapping("/importsTemplate.html")
    public String toImportsTemplate() {
        return "template/importsTemplate";
    }

    @RequestMapping("/downTemplate")
    public ResponseEntity<byte[]> downTemplate() throws IOException {
        return super.downTemplateEXCLX(CommonData.QUESTION_XLSX, CommonData.DOWN_QUESTION_XLSX);
    }

    @RequestMapping("/importQuestions")
    public String importQuestions(MultipartFile excel, String id, HttpSession session, Model model) throws IOException {
        String cacheKey = DEFAULT_QUESTION_SESSION;
        if (id != null && !"".equals(id)) {
            //编辑
            cacheKey += id;
        }
        List<Question> questionCache = getSessionValue(session, cacheKey);
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        //默认读取第一个sheet表数据
        ExcelReader reader = ExcelUtil.getReader(excel.getInputStream());
        //由于考题中，答案和选项需要单独的处理，不能简单的组成Question对象
        //所以不能直接利用hutool工具轻松完成装载
        //需要利用原生poi的技术一次处理每一个单元格数据
        Sheet sheet = reader.getSheet();

        List<QuestionVO> vos = new ArrayList<>();
        int count1 = 0;
        int count2 = 0;
        StringBuilder msg = new StringBuilder();

        question:
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Question question = new Question();
            //excel中所有的数字都是浮点型 ， 序号1--->1.0
            String no = row.getCell(0).toString().replace(".0", "");
            String type = row.getCell(1).toString();
            String level = row.getCell(2).toString();
            String subject = row.getCell(3).toString();

            String answer = "";
            String $answer = row.getCell(4).toString();
            if ("单选题".equals(type)) {
                //答案只有一个，需要判断其格式是否是ABCD字母，如果时，最终需要获得对应的是最(0123)
                int flag = checkAnswer12($answer);
                if (flag == -1) {
                    count2++;
                    msg.append("第【" + no + "】号试题，答案格式有问题|");
                    continue;
                }
                answer = String.valueOf(flag);
            }

            if ("多选题".equals(type)) {
                String[] answerArray = $answer.split("[,，]");
                for (String an : answerArray) {
                    int flag = checkAnswer12(an);
                    if (flag == -1) {
                        //答案有问题
                        count2++;
                        msg.append("第【" + no + "】号试题，答案格式有问题|");
                        continue question;
                    }
                    answer += String.valueOf(flag) + CommonData.SEPARATOR;
                }
            }

            if ("判断题".equals(type)) {
                int flag = checkAnswer3($answer);
                if (flag == -1) {
                    count2++;
                    msg.append("第【" + no + "】号试题，答案格式有问题|");
                    continue;
                }
                answer = String.valueOf(flag);
            }

            if ("填空题".equals(type)) {
                String[] answerArray = $answer.split("[,，]");
                if (!checkAnswer4(answerArray.length, subject)) {
                    count2++;
                    msg.append("第【" + no + "】号试题，答案数量有问题|");
                    continue;
                }

                for (String an : answerArray) {
                    answer += an + CommonData.SEPARATOR;
                }
            }

            if ("综合题".equals(type)) {
                answer = $answer;
            }

            //到此为止，答案就处理完毕了。接下来还有选择题的选项
            String options = "";
            if ("单选题".equals(type) || "多选题".equals(type)) {
                for (int j = 5; j <= row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        continue;
                    }
                    options += cell.toString().trim() + CommonData.SEPARATOR;
                }
            }

            //至此excel中就读取了一个question信息
            question.setStatus(CommonData.DEFAULT_QUESTION_STATUS);
            question.setTid(teacher.getId());
            question.setType(type);
            question.setSubject(subject);
            question.setLevel(level);
            question.setAnswer(answer);
            question.setOptions(options);

            questionCache.add(question);

            QuestionVO questionVO = questionCast(question, questionCache.size());
            vos.add(questionVO);
            count1++;
        }

        if (count2 == 0) {
            msg.append("成功导入考题【" + count1 + "】道");
        } else {
            msg.insert(0, "共导入考题【" + (count1 + count2) + "】道|成功添加考题【" + count1 + "】道|添加失败【" + count2 + "】道|");
        }
        model.addAttribute("questions", vos);
        model.addAttribute("msg", msg);
        return "template/questionViewTemplate::questionView";
    }

    /**
     * 检测填空题答案和空的数量是否匹配
     *
     * @return
     */
    private boolean checkAnswer4(int answer_count, String subject) {
        int count = 0;
        int i = 0;
        while ((i = subject.indexOf("【】")) != -1) {
            count++;
            subject = subject.substring(i + 1);
        }
        return answer_count == count;
    }

    /**
     * 检验判断题答案格式
     * 正确 返回0 or 1
     * 错误 返回-1
     *
     * @return
     */
    private int checkAnswer3(String an) {
        an = an.replace(" ", "");
        if ("正确".equals(an)) {
            return 0;
        } else if ("错误".equals(an)) {
            return 1;
        } else {
            return -1;
        }
    }


    /**
     * 检验选择题答案格式
     * 正确，返回字母对应的数字  A-0 ， B-1
     * 错误，返回-1
     *
     * @param c
     * @return
     */
    private int checkAnswer12(String c) {
        c = c.replace(" ", "");

        if (c.length() != 1) {
            //不是一个符号
            return -1;
        }
        if (!CommonData.OPTION_STR.contains(c)) {
            //不是字母
            return -1;
        }
        return c.charAt(0) - 'A';
    }

    @RequestMapping("/cancelSave")
    @ResponseBody
    public void cancelSave(HttpSession session, String id) {
        String cacheKey = DEFAULT_QUESTION_SESSION;
        if (id != null && !"".equals(id)) {
            //编辑
            cacheKey += id;
        }
        List list = getSessionValue(session, cacheKey);
        list.clear();
        //session.removeAttribute("questionCache");
    }

    @RequestMapping("/static/save")
    @ResponseBody
    public boolean staticSave(String course, Template template, HttpSession session) {
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        //完善模板
        template.setStatus(CommonData.DEFAULT_TEMPLATE_STATUS);
        template.setTid(teacher.getId());
        template.setYl1(course);

        List<Question> questionCache = getSessionValue(session, DEFAULT_QUESTION_SESSION);
        //先存储考题，获得id，再存模板
        for (Question question : questionCache) {
            question.setCourse(template.getYl1());
            questionService.save(question);
            switch (question.getType()) {
                case "单选题":
                    template.setQuestion1(template.getQuestion1() + CommonData.SEPARATOR + question.getId());
                    break;
                case "多选题":
                    template.setQuestion2(template.getQuestion2() + CommonData.SEPARATOR + question.getId());
                    break;
                case "判断题":
                    template.setQuestion3(template.getQuestion3() + CommonData.SEPARATOR + question.getId());
                    break;
                case "填空题":
                    template.setQuestion4(template.getQuestion4() + CommonData.SEPARATOR + question.getId());
                    break;
                case "综合题":
                    template.setQuestion5(template.getQuestion5() + CommonData.SEPARATOR + question.getId());
                    break;
            }
        }

        try {
            templateService.save(template);
            //缓存的数据就需要清空
            questionCache.clear();
            //session.removeAttribute("questionCache");
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }

    }

    //-------------------------------------------

    @RequestMapping("/template.html")
    public String toTemplate(HttpSession session, @RequestParam Map condition, Model model) {
        //查询基本信息，课程 分享老师（过滤），模板信息（展示）
        List<String> courses = dictionaryService.findCourses();
        model.addAttribute("courses", courses);
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        List<Teacher> teachers = teacherService.findByShare(teacher.getId());
        model.addAttribute("teachers", teachers);
        //模板信息查询
        condition.put("tid", teacher.getId());

        PageVO pageVO = templateService.find(CommonData.DEFAULT_PACE, CommonData.DEFAULT_ROWS, condition);

        model.addAttribute("pageVO", pageVO);
        return "template/template";
    }

    @RequestMapping("/templateGrid.html")
    public String toTemplateGrid(int pageNo, @RequestParam Map condition, HttpSession session, Model model) {
        Teacher teacher = (Teacher) session.getAttribute("loginTeacher");
        //查当前模板信息
        condition.put("tid", teacher.getId());
        PageVO pageVO = templateService.find(pageNo, CommonData.DEFAULT_ROWS, condition);
        model.addAttribute("pageVO", pageVO);
        return "template/template::#part-2";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public boolean delete(Long id, HttpSession session) {
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);

        return templateService.delete(id, teacher.getId());

    }

    @RequestMapping("/changeLeave")
    @ResponseBody
    public void changeLeave(Long id) {
        templateService.changeStatus(id, "丢弃");
    }


    @RequestMapping("/changePublic")
    @ResponseBody
    public void changePublic(Long id) {
        templateService.changeStatus(id, "公有");
    }

    @RequestMapping("/teacherGrid.html")
    public String toTeacherGrid(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo, String tname, Model model) {
        PageVO pageVO = teacherService.find(pageNo, CommonData.DEFAULT_ROWS, tname);
        model.addAttribute("pageVO", pageVO);
        return "template/teacherGridTemplate";
    }

    @RequestMapping("/shareTemplate")
    @ResponseBody
    public boolean shareTemplate(Long templateId, Long teacherId, HttpSession session) {
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        if (teacher.getId().equals(teacherId)) {
            return false;
        }

        templateService.shareTemplate(templateId, teacherId);
        return true;
    }

    @RequestMapping("/edit.html")
    public String toEdit(Long id, Model model, HttpSession session) {
        String cacheKey = DEFAULT_QUESTION_SESSION + id;
        Template template = templateService.findById(id);

        //对template做一些处理，使得可以在页面进行展示。例如对question的拆分。
        TemplateFormVO vo = new TemplateFormVO(template);
        model.addAttribute("template", vo);
        List<String> courses = dictionaryService.findCourses();
        model.addAttribute("courses", courses);
        if (template.getType().equals("静态模板")) {

            //如果是静态模板，模板中存储着关联的考题信息
            //只需要将这些考题信息存入session缓存，以后就都可以展示了
            //要将vo中存储的题号，变成考题
            List<QuestionVO> questions = new ArrayList<>();
            List<Question> questionCache = getSessionValue(session, cacheKey);
            if (questionCache != null && questionCache.size() > 0) {
                //之前已经有缓存了
                int index = 1;
                for (Question question : questionCache) {
                    QuestionVO questionVO = questionCast(question, index++);
                    questions.add(questionVO);
                }
            } else {
                //之前没有缓存，这是第一次
                questionCache = new ArrayList<>();
                session.setAttribute(cacheKey, questionCache);
                List<Integer> qids = new ArrayList<>();
                qids.addAll(vo.getQuestion1());
                qids.addAll(vo.getQuestion2());
                qids.addAll(vo.getQuestion3());
                qids.addAll(vo.getQuestion4());
                qids.addAll(vo.getQuestion5());
                for (Integer qid : qids) {
                    Question question = questionService.findById(qid.longValue());
                    questionCache.add(question);
                    //将question->questionVO 在面展示
                    QuestionVO questionVO = questionCast(question, questionCache.size());
                    questions.add(questionVO);
                }
            }
            model.addAttribute("questions", questions);
        }
        return "template/edit";
    }

    @RequestMapping("/dynamic/update")
    @ResponseBody
    public boolean dynamicUpdate(@RequestParam Map<String, String> info) {
        log.debug("info [{}]", info);


        Template template = new Template();
        template.setId(Long.valueOf(info.get("id")));
        template.setName(info.get("name"));
        template.setTotalScore(Integer.parseInt(info.get("totalScore")));
        template.setYl1(info.get("course"));

        //单选题处理
        String score1 = info.get("score1");
        String count11 = info.get("count11");
        String count12 = info.get("count12");
        String count13 = info.get("count13");
        String question1 = score1 + CommonData.SEPARATOR + count11 + CommonData.SEPARATOR + count12 + CommonData.SEPARATOR + count13;
        template.setQuestion1(question1);

        //多选题处理
        String score2 = info.get("score2");
        String count21 = info.get("count21");
        String count22 = info.get("count22");
        String count23 = info.get("count23");
        String question2 = score2 + CommonData.SEPARATOR + count21 + CommonData.SEPARATOR + count22 + CommonData.SEPARATOR + count23;
        template.setQuestion2(question2);

        //判断题处理
        String score3 = info.get("score3");
        String count31 = info.get("count31");
        String count32 = info.get("count32");
        String count33 = info.get("count33");
        String question3 = score3 + CommonData.SEPARATOR + count31 + CommonData.SEPARATOR + count32 + CommonData.SEPARATOR + count33;
        template.setQuestion3(question3);

        //填空题处理
        String score4 = info.get("score4");
        String count41 = info.get("count41");
        String count42 = info.get("count42");
        String count43 = info.get("count43");
        String question4 = score4 + CommonData.SEPARATOR + count41 + CommonData.SEPARATOR + count42 + CommonData.SEPARATOR + count43;
        template.setQuestion4(question4);

        //综合题处理
        String score5 = info.get("score5");
        String count51 = info.get("count51");
        String count52 = info.get("count52");
        String count53 = info.get("count53");
        String question5 = score5 + CommonData.SEPARATOR + count51 + CommonData.SEPARATOR + count52 + CommonData.SEPARATOR + count53;
        template.setQuestion5(question5);

        try {
            templateService.update(template);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @RequestMapping("/static/update")
    @ResponseBody
    public boolean staticUpdate(String course, Template template, HttpSession session) {
        String cacheKey = DEFAULT_QUESTION_SESSION + template.getId();
        //完善模板
        template.setYl1(course);

        List<Question> questionCache = getSessionValue(session, cacheKey);
        //先存储考题，获得id，再存模板
        for (Question question : questionCache) {
            question.setCourse(template.getYl1());
            if (question.getId() != null) {
                //对原考题的修改
                questionService.update(question);
            } else {
                //一个新的考题
                questionService.save(question);
            }
            switch (question.getType()) {
                case "单选题":
                    template.setQuestion1(template.getQuestion1() + CommonData.SEPARATOR + question.getId());
                    break;
                case "多选题":
                    template.setQuestion2(template.getQuestion2() + CommonData.SEPARATOR + question.getId());
                    break;
                case "判断题":
                    template.setQuestion3(template.getQuestion3() + CommonData.SEPARATOR + question.getId());
                    break;
                case "填空题":
                    template.setQuestion4(template.getQuestion4() + CommonData.SEPARATOR + question.getId());
                    break;
                case "综合题":
                    template.setQuestion5(template.getQuestion5() + CommonData.SEPARATOR + question.getId());
                    break;
            }
        }
        try {
            templateService.update(template);
            //缓存的数据就需要清空
            questionCache.clear();
            //session.removeAttribute("questionCache");
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    private <T> T getSessionValue(HttpSession session, String sessionKey) {
        return (T) session.getAttribute(sessionKey);
    }
}
