package com.yjy.examonline.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.common.CommonUtil;
import com.yjy.examonline.dao.*;
import com.yjy.examonline.domain.*;
import com.yjy.examonline.domain.dto.StudentExamDTO;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.domain.vo.QuestionVO;
import com.yjy.examonline.domain.vo.StudentExamVO;
import com.yjy.examonline.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ExamServiceTmpl implements ExamService {

    private Logger log = LoggerFactory.getLogger(ExamServiceTmpl.class);
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private StudentExamMapper studentExamMapper;

    @Autowired
    private TemplateMapper templateMapper;

    /**
     * 这个线程池用你来并行批阅客观答案
     */
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    @Override
    public PageVO findByPage(int page, int rows, Map condition) {
        PageHelper.startPage(page, rows);
        List<Exam> exams = examMapper.find(condition);
        PageInfo info = new PageInfo(exams);

        return CommonUtil.pageCast(info, condition);
    }

    @Override
    public void update(Exam exam) {
        examMapper.updateByPrimaryKeySelective(exam);
    }

    @Override
    public Exam findById(Long id) {
        return examMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(Exam exam) {//DuplicateKeyException
        try {
            examMapper.insert(exam);
        } catch (DuplicateKeyException e) {
            throw e;
        }

    }

    @Override
    public List<Map> findRefClasses(Long examId) {
        return studentExamMapper.findRefClasses(examId);
    }

    @Override
    public List<Map<String, Object>> findBindStudents(String className, String[] sidArray) {
        return studentMapper.findBindStudents(className, sidArray);
    }

    @Override
    public List<Map<String, Object>> findUnbindStudents(String className, String[] sidBindArray) {
        return studentMapper.findUnbindStudents(className, sidBindArray);
    }

    @Override
    public String findClassAllStudentIds(String className) {
        return studentMapper.findClassAllStudentIds(className);
    }

    @Override
    public void fill(Exam exam, Map<String, String> classesCache) {
        //先对exam对象做修改 (templateId , duration , startTime , endTime)
        examMapper.updateByPrimaryKeySelective(exam);

        //更新学生关联信息
        //先获得当前考试的上一次的关联信息
        List<Long> oldSidList = studentExamMapper.findStudentIdByExam(exam.getId());
        //接下来就需要将oldSidList中的学生id 和 classesCache中的学生id比对
        //找到多的增加，找到少的删除
        List<StudentExam> addList = new ArrayList<>();
        List<StudentExam> removeList = new ArrayList<>();

        //比较时，需要先将缓存中的信息变成学生id列表。
        //逐个的将班级中的人员信息取出
        String[] classNameArray = new String[classesCache.size()];
        classesCache.keySet().toArray(classNameArray);
        //学生与班级的对应关系
        //key = set 装载班级关联的学生id
        //value = string 存储班级名称
        Map<Set, String> map = new HashMap<>();
        for (String className : classNameArray) {
            // 3种 "ALL","1,2,3,4,5","x,1,2,3,4,5"
            String info = classesCache.get(className);
            if ("ALL".equals(info)) {
                //获取这个班级所有的学生id组成的字符串 ALL->"1,2,3,4,5,6"
                info = studentMapper.findClassAllStudentIds(className);
            } else if (info.startsWith("x")) {
                //"x,1,2,3" --> "1,2,3"
                info = info.replace("x,", "");
            }

            Set<String> idSet = new HashSet<>(Arrays.asList(info.split(",")));
            map.put(idSet, className);
        }

        //代码至此，就将新数据以{set学生：班级}装载
        //接下来获得需要增加和删除的学生id
        Set<Set> sets = map.keySet();

        //装载所有新学生的id
        Set<String> newSidSet = new HashSet<String>();
        for (Set set : sets) {
            newSidSet.addAll(set);
        }

        //装载上一次学生的id
        Set<String> oldSidSet = new HashSet<>();
        for (Long sid : oldSidList) {
            oldSidSet.add(String.valueOf(sid));
        }

        Set<String> all = new HashSet<>();
        all.addAll(newSidSet);
        all.addAll(oldSidSet);

        //此时all中存储的就是需要增加的学生信息
        all.removeAll(oldSidSet);
        Long examId = exam.getId();
        for (String sid : all) {
            StudentExam se = new StudentExam();
            addList.add(se);
            se.setStatus(CommonData.DEFAULT_STUDENT_EXAM_STATUS);
            se.setExamId(examId);
            se.setStudentId(Long.valueOf(sid));
            for (Set set : sets) {
                if (set.contains(sid)) {
                    String className = map.get(set);
                    se.setExamGroup(className);
                    break;
                }
            }
        }

        all.addAll(oldSidSet);
        //此时all中储存胡的就是需要删除的学生信息
        all.removeAll(newSidSet);
        for (String sid : all) {
            StudentExam se = new StudentExam();
            removeList.add(se);
            se.setExamId(examId);
            se.setStudentId(Long.valueOf(sid));
        }

        //移除
        if (removeList.size() > 0) {
            studentExamMapper.removeReference(removeList);
        }

        //添加
        if (addList.size() > 0) {
            studentExamMapper.addReference(addList);
        }
    }

    @Override
    public boolean isPageExist(Long id) {
        return studentExamMapper.findPagePathCount(id) > 0;
    }

    @Override
    public void generatePage(Long id) {
        Exam exam = examMapper.selectByPrimaryKey(id);

        String dirName = exam.getName();
        String dirPath = CommonData.PAGE_ROOT_PATH + File.separator + dirName;

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //至此，存放考卷的目录已存在

        //先获得模板，根据考试模板找到考题，将考题写入考卷文件
        Template template = templateMapper.selectByPrimaryKey(exam.getTemplateId());
        if ("静态模板".equals(template.getType())) {
            //静态模板考题处理
            List<Question> questionList = new ArrayList<>();
            File pageFile = new File(dir, CommonData.STATIC_PAGE_NAME);

            //单选题 2}-|-{1}-|-{2}-|-{3}-|-{4
            String questionStr = template.getQuestion1();
            loadStaticQuestion(questionList, questionStr);

            //多选题 2}-|-{1}-|-{2}-|-{3}-|-{4
            questionStr = template.getQuestion2();
            loadStaticQuestion(questionList, questionStr);

            //判断题 2}-|-{1}-|-{2}-|-{3}-|-{4
            questionStr = template.getQuestion3();
            loadStaticQuestion(questionList, questionStr);

            //填空题 2}-|-{1}-|-{2}-|-{3}-|-{4
            questionStr = template.getQuestion4();
            loadStaticQuestion(questionList, questionStr);

            //综合题 2}-|-{1}-|-{2}-|-{3}-|-{4
            questionStr = template.getQuestion5();
            loadStaticQuestion(questionList, questionStr);

            //代码至此，静态试卷信息就准备就绪，可以生成了
            writePage(pageFile, questionList);

            //需要将考卷的路径更新至考生的page_path字段中
            //静态考卷是全部
            //f:/z/xxxxx/page.txt
            String path = pageFile.getAbsolutePath();
            // /xxxx/page.txt
            path = path.replace(CommonData.PAGE_ROOT_PATH, "");
            studentExamMapper.updatePagePath(exam.getId(), null, path);

        } else {
            //动态模板考试题处理
            log.info("============开始生成动态考卷=============");
            ExecutorService executorService = Executors.newCachedThreadPool();
            List<Future> submits = new ArrayList<>();
            List<Student> students = studentMapper.findByExam(exam.getId());
            int threadCount = 5;
            if (students.size() >= threadCount) {
                //利用10个线程
                //先分组
                List<List<Student>> studentsAll = new ArrayList<>();
                for (int i = 0; i < threadCount; i++) {
                    studentsAll.add(new ArrayList<Student>());
                }
                int index = 0;
                for (Student student : students) {
                    studentsAll.get(index).add(student);
                    index = index == threadCount - 1 ? 0 : ++index;
                }
                for (int i = 0; i < threadCount; i++) {
                    //new Thread(new DynamicPageGenerator(dir,studentsAll.get(i),template,exam)).start();
                    Future<?> submit = executorService.submit(new DynamicPageGenerator(dir, studentsAll.get(i), template, exam));
                    submits.add(submit);
                }

            } else {
                //利用1个线程
                //new Thread(new DynamicPageGenerator(dir,students,template,exam)).start();
                Future<?> submit = executorService.submit(new DynamicPageGenerator(dir, students, template, exam));
                submits.add(submit);
            }

            //业务线程实时监控子线程是否执行完毕。当都执行完毕时，再给与反馈
            out:
            while (true) {
                for (Future submit : submits) {
                    if (submit.isDone()) {
                        continue;
                    }
                    //发现有一个线程还没有执行完毕，其他线程是否执行完毕就无所谓了。
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //等一小下，重新检查
                    continue out;
                }
                //完成了一次submit循环验证，证明所有的submit都执行完毕了
                break;
            }
            log.info("============动态考卷生成完毕=============");

        }

    }

    private void loadStaticQuestion(List<Question> questionList, String questionStr) {
        String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
        for (int i = 1; i < array.length; i++) {
            String qid = array[i];
            Question question = questionMapper.selectByPrimaryKey(Long.valueOf(qid));
            //使用预留4存储考题分数
            question.setYl4(array[0]);
            questionList.add(question);
        }
    }

    private void writePage(File pageFile, List<Question> questionList) {
        try {
            //创建试卷文件
            pageFile.createNewFile();

            FileWriter w = new FileWriter(pageFile);

            for (Question question : questionList) {
                w.write(question.getType());
                w.write(CommonData.QUESTION_OPTION_SEPARATOR);
                w.write(question.getYl4());
                w.write(CommonData.QUESTION_OPTION_SEPARATOR);
                w.write(question.getSubject());
                w.write(CommonData.QUESTION_OPTION_SEPARATOR);
                w.write(question.getOptions());
                w.write(CommonData.QUESTION_OPTION_SEPARATOR);
                w.write(question.getAnswer());
                w.write(CommonData.QUESTION_OPTION_SEPARATOR);

                w.write(CommonData.QUESTION_SEPARATOR);
                w.write(CommonData.QUESTION_OPTION_SEPARATOR);
            }

            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Question> questions11;
    private static List<Question> questions12;
    private static List<Question> questions13;
    private static List<Question> questions21;
    private static List<Question> questions22;
    private static List<Question> questions23;
    private static List<Question> questions31;
    private static List<Question> questions32;
    private static List<Question> questions33;
    private static List<Question> questions41;
    private static List<Question> questions42;
    private static List<Question> questions43;
    private static List<Question> questions51;
    private static List<Question> questions52;
    private static List<Question> questions53;

    /**
     * 内部类，线程类
     * 利用多线程完成动态试卷的生成
     */
    private class DynamicPageGenerator implements Runnable {
        File dir;
        List<Student> students;
        Template template;
        Exam exam;

        public DynamicPageGenerator(File dir, List<Student> students, Template template, Exam exam) {
            this.dir = dir;
            this.students = students;
            this.template = template;
            this.exam = exam;
        }

        private Random random = new Random();
        Set<Integer> checkbox = new HashSet<>();

        @Override
        public void run() {
            for (Student student : students) {
                //装载当前学生的考题
                List<Question> questionList = new ArrayList<>();

                //单选题 2}-|-{2}-|-{2}-|-{3
                {
                    String questionStr = template.getQuestion1();
                    String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
                    String score = array[0];
                    int count1 = Integer.parseInt(array[1]);
                    int count2 = Integer.parseInt(array[2]);
                    int count3 = Integer.parseInt(array[3]);
                    if (count1 > 0) {
                        //先获得所有的指定课程的简单单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions11 == null) {
                            synchronized ("dmc11") {
                                if (questions11 == null) {
                                    questions11 = questionMapper.findByTypeAndStatusAndCourse("单选题", "简单", template.getYl1());
                                }
                            }
                        }
                        int count = questions11.size();
                        int _count = 0; //抽取考题的数量
                        while (_count < count1) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions11.get(i);
                            question.setYl4(score);
                            questionList.add(question);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count2 > 0) {
                        //先获得所有的指定课程的中等单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions12 == null) {
                            synchronized ("dmc12") {
                                if (questions12 == null) {
                                    questions12 = questionMapper.findByTypeAndStatusAndCourse("单选题", "中等", template.getYl1());
                                }
                            }
                        }
                        int count = questions12.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count2) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions12.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count3 > 0) {
                        //先获得所有的指定课程的困难单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions13 == null) {
                            synchronized ("dmc13") {
                                if (questions13 == null) {
                                    questions13 = questionMapper.findByTypeAndStatusAndCourse("单选题", "困难", template.getYl1());
                                }
                            }
                        }
                        int count = questions13.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count3) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions13.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                }
                //==================================================
                //多选题 2}-|-{2}-|-{2}-|-{3
                {
                    String questionStr = template.getQuestion2();
                    String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
                    String score = array[0];
                    int count1 = Integer.parseInt(array[1]);
                    int count2 = Integer.parseInt(array[2]);
                    int count3 = Integer.parseInt(array[3]);
                    if (count1 > 0) {
                        //先获得所有的指定课程的简单单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions21 == null) {
                            synchronized ("dmc21") {
                                if (questions21 == null) {
                                    questions21 = questionMapper.findByTypeAndStatusAndCourse("多选题", "简单", template.getYl1());
                                }
                            }
                        }
                        int count = questions21.size();
                        int _count = 0; //抽取考题的数量
                        while (_count < count1) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions21.get(i);
                            question.setYl4(score);
                            questionList.add(question);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count2 > 0) {
                        //先获得所有的指定课程的中等单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions22 == null) {
                            synchronized ("dmc22") {
                                if (questions22 == null) {
                                    questions22 = questionMapper.findByTypeAndStatusAndCourse("多选题", "中等", template.getYl1());
                                }
                            }
                        }
                        int count = questions22.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count2) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions22.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count3 > 0) {
                        //先获得所有的指定课程的困难单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions23 == null) {
                            synchronized ("dmc23") {
                                if (questions23 == null) {
                                    questions23 = questionMapper.findByTypeAndStatusAndCourse("多选题", "困难", template.getYl1());
                                }
                            }
                        }
                        int count = questions23.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count3) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions23.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                }
                //==================================================
                //判断题 2}-|-{2}-|-{2}-|-{3
                {
                    String questionStr = template.getQuestion3();
                    String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
                    String score = array[0];
                    int count1 = Integer.valueOf(array[1]);
                    int count2 = Integer.valueOf(array[2]);
                    int count3 = Integer.valueOf(array[3]);
                    if (count1 > 0) {
                        //先获得所有的指定课程的简单单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions31 == null) {
                            synchronized ("dmc31") {
                                if (questions31 == null) {
                                    questions31 = questionMapper.findByTypeAndStatusAndCourse("判断题", "简单", template.getYl1());
                                }
                            }
                        }
                        int count = questions31.size();
                        int _count = 0; //抽取考题的数量
                        while (_count < count1) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions31.get(i);
                            question.setYl4(score);
                            questionList.add(question);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count2 > 0) {
                        //先获得所有的指定课程的中等单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions32 == null) {
                            synchronized ("dmc32") {
                                if (questions32 == null) {
                                    questions32 = questionMapper.findByTypeAndStatusAndCourse("判断题", "中等", template.getYl1());
                                }
                            }
                        }
                        int count = questions32.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count2) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions32.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count3 > 0) {
                        //先获得所有的指定课程的困难单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions33 == null) {
                            synchronized ("dmc33") {
                                if (questions33 == null) {
                                    questions33 = questionMapper.findByTypeAndStatusAndCourse("判断题", "困难", template.getYl1());
                                }
                            }
                        }
                        int count = questions33.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count3) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions33.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                }
                //==================================================
                //填空题 2}-|-{2}-|-{2}-|-{3
                {
                    String questionStr = template.getQuestion4();
                    String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
                    String score = array[0];
                    int count1 = Integer.parseInt(array[1]);
                    int count2 = Integer.parseInt(array[2]);
                    int count3 = Integer.parseInt(array[3]);
                    if (count1 > 0) {
                        //先获得所有的指定课程的简单单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions41 == null) {
                            synchronized ("dmc41") {
                                if (questions41 == null) {
                                    questions41 = questionMapper.findByTypeAndStatusAndCourse("填空题", "简单", template.getYl1());
                                }
                            }
                        }
                        int count = questions41.size();
                        int _count = 0; //抽取考题的数量
                        while (_count < count1) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions41.get(i);
                            //检测当前这道题空的数量
                            String answerStr = question.getAnswer();
                            String[] answerArray = answerStr.split(CommonData.SPLIT_SEPARATOR);
                            if (answerArray.length <= (count1 - _count)) {
                                //此时这道题是可用的
                                question.setYl4(score);
                                questionList.add(question);
                                _count += answerArray.length;
                            }

                            checkbox.add(i);


                        }
                        checkbox.clear();
                    }
                    if (count2 > 0) {
                        //先获得所有的指定课程的中等单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions42 == null) {
                            synchronized ("dmc42") {
                                if (questions42 == null) {
                                    questions42 = questionMapper.findByTypeAndStatusAndCourse("填空题", "中等", template.getYl1());
                                }
                            }
                        }
                        int count = questions42.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count2) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions42.get(i);
                            //检测当前这道题空的数量
                            String answerStr = question.getAnswer();
                            String[] answerArray = answerStr.split(CommonData.SPLIT_SEPARATOR);
                            if (answerArray.length <= (count2 - _count)) {
                                //此时这道题是可用的
                                question.setYl4(score);
                                questionList.add(question);
                                _count += answerArray.length;
                            }

                            checkbox.add(i);
                        }
                        checkbox.clear();
                    }
                    if (count3 > 0) {
                        //先获得所有的指定课程的困难单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions43 == null) {
                            synchronized ("dmc43") {
                                if (questions43 == null) {
                                    questions43 = questionMapper.findByTypeAndStatusAndCourse("填空题", "困难", template.getYl1());
                                }
                            }
                        }
                        int count = questions43.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count3) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions43.get(i);
                            //检测当前这道题空的数量
                            String answerStr = question.getAnswer();
                            String[] answerArray = answerStr.split(CommonData.SPLIT_SEPARATOR);
                            if (answerArray.length <= (count3 - _count)) {
                                //此时这道题是可用的
                                question.setYl4(score);
                                questionList.add(question);
                                _count += answerArray.length;
                            }

                            checkbox.add(i);
                        }
                        checkbox.clear();
                    }
                }
                //==================================================
                //综合题 2}-|-{2}-|-{2}-|-{3
                {
                    String questionStr = template.getQuestion5();
                    String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
                    String score = array[0];
                    int count1 = Integer.parseInt(array[1]);
                    int count2 = Integer.parseInt(array[2]);
                    int count3 = Integer.parseInt(array[3]);
                    if (count1 > 0) {
                        //先获得所有的指定课程的简单单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions51 == null) {
                            synchronized ("dmc51") {
                                if (questions51 == null) {
                                    questions51 = questionMapper.findByTypeAndStatusAndCourse("综合题", "简单", template.getYl1());
                                }
                            }
                        }
                        int count = questions51.size();
                        int _count = 0; //抽取考题的数量
                        while (_count < count1) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions51.get(i);
                            question.setYl4(score);
                            questionList.add(question);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count2 > 0) {
                        //先获得所有的指定课程的中等单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions52 == null) {
                            synchronized ("dmc52") {
                                if (questions52 == null) {
                                    questions52 = questionMapper.findByTypeAndStatusAndCourse("综合题", "中等", template.getYl1());
                                }
                            }
                        }
                        int count = questions52.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count2) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions52.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                    if (count3 > 0) {
                        //先获得所有的指定课程的困难单选题
                        //此时确保10个线程只会获取一次缓存信息
                        if (questions53 == null) {
                            synchronized ("dmc53") {
                                if (questions53 == null) {
                                    questions53 = questionMapper.findByTypeAndStatusAndCourse("综合题", "困难", template.getYl1());
                                }
                            }
                        }
                        int count = questions53.size();
                        int _count = 0; //抽取考题的数量

                        while (_count < count3) {
                            int i;
                            do {
                                i = random.nextInt(count);// [0,count)
                            } while (checkbox.contains(i));

                            Question question = questions53.get(i);
                            questionList.add(question);
                            question.setYl4(score);
                            checkbox.add(i);
                            _count++;
                        }
                        checkbox.clear();
                    }
                }

                //代码至此，一个学生的考题就抽取完毕了
                String fileName = student.getCode() + "_" + student.getSname() + ".txt";
                File pageFile = new File(dir, fileName);
                ExamServiceTmpl.this.writePage(pageFile, questionList);

                //同时需要将路径更新至数据库
                //f:/z/xxxxx/page.txt
                String path = pageFile.getAbsolutePath();
                // /xxxx/page.txt
                path = path.replace(CommonData.PAGE_ROOT_PATH, "");
                //path: /+string
                studentExamMapper.updatePagePath(exam.getId(), student.getId(), path);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean removePage(Long id) {
        Exam exam = examMapper.selectByPrimaryKey(id);
        if (!"未发布".equals(exam.getStatus())) {
            return false;
        }
        //删除试卷路径
        studentExamMapper.removePagePath(id);
        //删除试卷
        String dirName = CommonData.PAGE_ROOT_PATH + File.separator + exam.getName();
        File dir = new File(dirName);
        removePage(dir);
        return true;
    }

    @Override
    public boolean removeExam(Long id) {
        Exam exam = examMapper.selectByPrimaryKey(id);
        if (!"未发布".equals(exam.getStatus())) {
            return false;
        }
        //删除考试信息
        examMapper.deleteByPrimaryKey(id);
        //删除考试关联的学生信息
        studentExamMapper.removeRefStudentsByExam(id);
        //删除考试试卷
        String dirName = CommonData.PAGE_ROOT_PATH + File.separator + exam.getName();
        File dir = new File(dirName);
        removePage(dir);
        dir.delete();

        return true;
    }

    private void removePage(File dir) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    @Override
    public void releaseExam(Long id) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setStatus("未考试");
        examMapper.updateByPrimaryKeySelective(exam);
    }

    @Override
    public List<Map> findByStudent(Long sid, Integer timeFlag) {
        return examMapper.findByStudent(sid, timeFlag);
    }

    @Override
    public StudentExam findStudentExamById(Long studentId, Long examId) {
        StudentExam se = studentExamMapper.findStudentExamById(examId, studentId);
        return se;
    }

    @Override
    public void update(StudentExam se) {
        studentExamMapper.updateByPrimaryKeySelective(se);
    }

    //缓存考试的开始状态的变化
    //key = flag + examId
    private static Map<String, Integer> flagMap = new HashMap<>();

    @Override
    public void startExam(Long studentId, Long examId) {

        studentExamMapper.changeStatus(examId, studentId, "考试中");

        //还需要检测当前考试状态是否需要改变。
        String key = "flag" + examId;
        Integer flag = flagMap.get(key);
        if (flag == null || flag == 0) {
            //考试状态还没有改变
            synchronized ("startExam") {
                if (flag == null || flag == 0) {
                    examMapper.changeStatus(examId, "考试中");
                    flagMap.put(key, 1);
                }
            }
        }
    }

    @Override
    public void updateStartTime(Long studentId, Long examId) {
        studentExamMapper.updateStartTime(examId, studentId);
    }

    @Override
    public void updateAnswer(Map answerInfo) {
        studentExamMapper.updateAnswer(answerInfo);
    }


    Map<Long, Exam> examCache = new HashMap<>();

    @Override
    public void submitPage(Map answerInfo) {
        //更新答案
        studentExamMapper.updateAnswer(answerInfo);

        //更新状态
        StudentExam se = new StudentExam();
        Long examId = Long.valueOf(answerInfo.get("examId").toString());
        se.setExamId(examId);
        se.setStudentId(Long.valueOf(answerInfo.get("studentId").toString()));
        se.setStatus("已完成");
        Date curr = new Date();
        se.setEndTime(curr);

        studentExamMapper.updateByPrimaryKeySelective(se);

        //计算当前这一个学生的客观题分数
        pool.execute(() -> {
            //此时查询考生的考试信息可能还是考试中
            StudentExam se1 = studentExamMapper.findStudentExamById(se.getExamId(), se.getStudentId());
            calculateObjectiveQuestion(se1);
        });

        //检测更新本轮考试的状态
        //  时长考试，由管理员手动控制完成状态
        //  区间考试，可以自动控制完成状态
        //所以需要知道本轮考试的信息
        //  如果每个同学交卷时都查询一次数据库，会影响性能。 因为改变本轮考试状态的操作一次就够了
        //  可以使用缓存
        Exam exam = examCache.get(examId);
        if (exam == null) {
            exam = examMapper.selectByPrimaryKey(examId);
        }

        if (exam.getDuration() != null && !"".equals(exam.getDuration())) {
            //时长考试
            return;
        }
        //区间考试
        Date endTime = exam.getEndTime();
        if (!curr.before(endTime) && !"已完成".equals(exam.getStatus())) {
            //超时了，本轮考试也已经结束了。 同时证明不是提前交卷。
            exam.setStatus("已完成");
            examMapper.updateByPrimaryKeySelective(exam);
            //所有这个考试的学生，如果没有答卷，需要将状态改为缺考。
            handleStudentStatusForFinish(examId);
        }
    }

    private void handleStudentStatusForFinish(Long examId) {
        //提前计算一下所有断线同学的客观题分数（处于考试中，又不能自己完成的）
        //找到考试中的那些学生，然后利用多线程实现客观题分数的自动批阅
        //因为是多线程，所以对于接下来的考试状态的修改（已完成，缺考）没有影响
        List<StudentExam> students = studentExamMapper.findInterruptStudents(examId);
        for (StudentExam student : students) {
            pool.execute(() -> {
                calculateObjectiveQuestion(student);
            });
        }


        //处于考试中的学生，就可以设置为已完成(断线)
        studentExamMapper.updateFinishByExam(examId);
        //处于未考试的学生，就可以设置为缺考
        studentExamMapper.updateMissByExam(examId);
    }

    /**
     * 计算当前学生客观题分数
     */
    private void calculateObjectiveQuestion(StudentExam se) {
        String pagePath = se.getPagePath();
        pagePath = CommonData.PAGE_ROOT_PATH + pagePath;
        List<QuestionVO> questionVOS = CommonUtil.readPage(pagePath);

        //se中包含了自定义答案
        //这些答案被存储在5个属性中answer1-answer5
        //需要将其组成在一个集合中，并且与上面questionVOS对应
        StudentExamVO studentExamVO = new StudentExamVO(se);
        List<Object> answerList = studentExamVO.getAnswerList();


        //记录总分
        int totalSocre = 0;

        int index = 0;
        for (QuestionVO question : questionVOS) {
            String type = question.getType();
            if ("单选题".equals(type) || "判断题".equals(type)) {
                //只有1个答案
                String a1 = question.getAnswerList().get(0);
                String a2 = answerList.get(index).toString();
                if (a1.equals(a2)) {
                    //正确
                    totalSocre += question.getScore();
                }
            }

            if ("多选题".equals(type)) {
                //只有1个答案
                List<String> a1 = question.getAnswerList(); //["1","2"]
                Object ta2 = answerList.get(index);
                if (ta2.toString().equals("-1")) {
                    //这道多选题没有选择答案
                    continue;
                }
                //选了答案，需要判断答案对不对
                List<Integer> a2 = (List) ta2;//[1,2]

                //a1 和 a2 中存储的答案（选项序号），一定都是从小到大排列的。
                if (a1.size() != a2.size()) {
                    //答案数量不对
                    continue;
                }

                //答案数量对上了，内容需要逐一比较
                for (int i = 0; i < a1.size(); i++) {
                    if (!a1.get(i).equals(a2.get(i).toString())) {
                        //有一个答案不一样，此题不对
                        continue;
                    }
                }

                //代码至此，有答案，与正确答案数量一致，与正确答案内容一致
                totalSocre += question.getScore();
            }

            index++;
        }

        //代码至此，就计算了客观的分数，需要更新
        se.setScore(totalSocre);
        se.setStatus(null);
        studentExamMapper.updateByPrimaryKeySelective(se);
    }

    @Override
    public void leaveExam(Long id) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setStatus("丢弃");
        examMapper.updateByPrimaryKeySelective(exam);
    }

    @Override
    public void finishExam(Long id) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setStatus("已完成");
        examMapper.updateByPrimaryKeySelective(exam);
        //还需要对学生考试状态做处理。
        handleStudentStatusForFinish(id);
    }

    @Override
    public List<Map> findClassesByExam(Long id) {
        return studentExamMapper.findClassesByExam(id);
    }


    @Override
    public List<StudentExamDTO> findStudentsByExamAndClass(Long examId, String className) {
        return studentExamMapper.findStudentsByExamAndClass(examId, className);
    }

    @Override
    public void changeStudentStatus(Long examId, Long studentId, String status) {
        studentExamMapper.changeStatus(examId, studentId, status);
    }

    @Override
    public void review(StudentExam studentExam) {
        studentExamMapper.updateByPrimaryKeySelective(studentExam);
    }

    @Override
    public void submit(Long examId) {
        Exam exam = new Exam();
        exam.setId(examId);
        exam.setYl1("提交");
        examMapper.updateByPrimaryKeySelective(exam);
    }

    @Override
    public List<Integer> findStudentExamYears(Long studentId) {
        return studentExamMapper.findStudentExamYears(studentId);
    }

    @Override
    public List<StudentExamDTO> findStudentScores(Long studentId, Integer year) {
        return studentExamMapper.findStudentScores(studentId, year);
    }


}
