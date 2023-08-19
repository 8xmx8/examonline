package com.yjy.examonline.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.common.CommonUtil;
import com.yjy.examonline.domain.*;
import com.yjy.examonline.domain.dto.StudentExamDTO;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.domain.vo.QuestionVO;
import com.yjy.examonline.domain.vo.StudentExamVO;
import com.yjy.examonline.domain.vo.TemplateFormVO;
import com.yjy.examonline.service.*;
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
import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping("/exam")
public class ExamController extends BaseControllerAndUpload {

    private static final String cacheKey_prefix = "classesCache";

    @Autowired
    private ExamService examService;

    @Autowired
    private StudentService studentService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/exam.html")
    public String toExam(Model model, HttpSession session) {
        Teacher teacher = (Teacher) session.getAttribute("loginTeacher");
        Map condition = CommonData.DEFAULT_CONDITION;
        condition.put("tid", teacher.getId());

        PageVO vo = examService.findByPage(CommonData.DEFAULT_PACE, CommonData.DEFAULT_ROWS, condition);
        model.addAttribute("pageVO", vo);
        return "exam/exam";
    }

    @RequestMapping("/examGridTemplate.html")
    public String toExamGrid(int pageNo, @RequestParam Map condition, Model model, HttpSession session) {
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        condition.put("tid", teacher.getId());

        PageVO vo = examService.findByPage(pageNo, CommonData.DEFAULT_ROWS, condition);
        model.addAttribute("pageVO", vo);

        return "exam/exam::#part-2";
    }

    @RequestMapping("/form.html")
    public String toForm(Long id, Model model) {
        if (id != null) {
            //编辑时页面访问，需要获取原始数据
            Exam exam = examService.findById(id);
            model.addAttribute("exam", exam);
        }
        return "exam/form";
    }

    @RequestMapping("/save")
    @ResponseBody
    public boolean save(Exam exam, HttpSession session) {
        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        exam.setTid(teacher.getId());
        exam.setStatus(CommonData.DEFAULT_EXAM_STATUS);

        try {
            examService.save(exam);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public boolean update(Exam exam) {
        try {
            examService.update(exam);
            return true;
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping("/templateSelect.html")
    public String toTemplateSelect(HttpSession session, @RequestParam Map condition, Model model) {

        List<String> courses = dictionaryService.findCourses();
        model.addAttribute("courses", courses);

        Teacher teacher = getSessionValue(session, CommonData.DEFAULT_TEACHER_SESSION);
        List<Teacher> teachers = teacherService.findByShare(teacher.getId());
        model.addAttribute("teachers", teachers);

        condition.put("tid", teacher.getId());

        PageVO pageVO = templateService.find(CommonData.DEFAULT_PACE, CommonData.DEFAULT_ROWS, condition);
        model.addAttribute("pageVO", pageVO);

        return "template/template::#exam-use-template";
    }


    @RequestMapping("/fill.html")
    public String toFill(Long id, Model model, HttpSession session) {
        String cacheKey = "classesCache" + id;

        Exam exam = examService.findById(id);
        model.addAttribute("exam", exam);

        //缺模板名称
        if (exam.getTemplateId() != null) {
            Template template = templateService.findById(exam.getTemplateId());
            model.addAttribute("template", template);
        }

        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        if (classesCache == null) {
            classesCache = new HashMap<>();
            session.setAttribute(cacheKey, classesCache);

            //此时缓存中没有数据，应该是首次进入当前页面
            //需要将持久化的关联信息取出，并装入缓存。{"班级名","1,2,3,4,5"}
            List<Map> refClasses = examService.findRefClasses(exam.getId());
            for (Map map : refClasses) {
                Object key = map.get("exam_group");
                Object value = map.get("sids");
                classesCache.put(key.toString(), value.toString());
            }
        }

        if (classesCache.size() > 0) {
            List<Map> refClasses = loadRefClasses(classesCache);
            model.addAttribute("refClasses", refClasses);
        }


        //现在考试信息有不同的状态，未发布时，可以修改。 发布后就只能查看
        if (exam.getStatus().equals("未发布")) {
            return "exam/fill";
        } else {
            return "exam/fillInfo";
        }

    }

    @RequestMapping("/templateDetail.html")
    public String toTemplateDetail(Long templateId, Model model) {
        Template template = templateService.findById(templateId);

        //对template做一些处理，使得可以在页面进行展示。例如对question的拆分。
        TemplateFormVO vo = new TemplateFormVO(template);
        model.addAttribute("template", vo);


        //如果是静态模板，模板中存储着关联的考题信息

        if (vo.getType().equals("静态模板")) {
            //要将vo中存储的题号，变成考题
            List<QuestionVO> questions = new ArrayList<QuestionVO>();
            List<Integer> qids = new ArrayList<>();
            qids.addAll(vo.getQuestion1());
            qids.addAll(vo.getQuestion2());
            qids.addAll(vo.getQuestion3());
            qids.addAll(vo.getQuestion4());
            qids.addAll(vo.getQuestion5());
            int index = 1;
            for (Integer qid : qids) {
                Question question = questionService.findById(qid.longValue());

                //将question->questionVO 在面展示
                QuestionVO questionVO = CommonUtil.questionCast(question, index++);
                questions.add(questionVO);
            }
            model.addAttribute("questions", questions);
        }

        return "template/edit::#exam-use-template-info";
    }

    @RequestMapping("/selectClasses.html")
    public String toSelectClasses() {
        return "student/student::#exam-use-classes-students";
    }

    @RequestMapping("/bindClasses")
    @ResponseBody
    public void bindClasses(Long id, String classNames, HttpSession session) {
        String cacheKey = "classesCache" + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        if (classesCache == null) {
            classesCache = new HashMap<>();
            session.setAttribute(cacheKey, classesCache);
        }


        log.debug("binding start:" + cacheKey + " " + classesCache);

        String[] classNameArray = classNames.split(",");
        for (String className : classNameArray) {
            classesCache.put(className, "ALL");
        }

        log.debug("binding end:" + cacheKey + " " + classesCache);
    }

    @RequestMapping("/unbindClasses")
    @ResponseBody
    public void unbindClasses(Long id, String classNames, HttpSession session) {
        String cacheKey = "classesCache" + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);


        log.debug("unbinding start:" + cacheKey + " " + classesCache);

        String[] classNameArray = classNames.split(",");
        for (String className : classNameArray) {
            classesCache.remove(className);
        }

        log.debug("unbinding end:" + cacheKey + " " + classesCache);
    }

    @RequestMapping("/bindStudent")
    @ResponseBody
    public void bindStudent(Long id, String className, Long studentId, HttpSession session) {
        String cacheKey = "classesCache" + id;

        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        if (classesCache == null) {
            classesCache = new HashMap<>();
            session.setAttribute(cacheKey, classesCache);
        }

        log.debug("binding start:" + cacheKey + " " + classesCache);

        String classNameInfo = classesCache.get(className);
        if (classNameInfo == null) {
            //首次绑定这个班级，这个班级目前只有这一个学生。
            classesCache.put(className, String.valueOf(studentId));
        } else {
            //这个班级存在，增加这个学生
            classesCache.put(className, classNameInfo + "," + studentId);
        }

        log.debug("binding end:" + cacheKey + " " + classesCache);

    }


    @RequestMapping("/unbindStudent")
    @ResponseBody
    public void unbindStudent(Long id, String className, Long studentId, HttpSession session) {
        String cacheKey = "classesCache" + id;

        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);

        log.debug("unbinding start:" + cacheKey + " " + classesCache);

        String classNameInfo = classesCache.get(className);
        if (classNameInfo.equals("ALL")) {
            //之前绑定了所有的学生，现在解绑一个，存储信息就需要从ALL ->1,2,4,5,6
            //需要先将ALL->完整的id字符串 1,2,3,4,5,6 -> 再移除 1,2,4,5,6
            String studentStr = studentService.findStudentIdsExcludeId(className, studentId);
            classesCache.put(className, studentStr);
        } else {
            //之前也是绑定一部分学生（1,2,3,4,5）
            List<String> list = Arrays.asList(classNameInfo.split(","));
            Set<String> set = new HashSet<>(list);
            set.remove(String.valueOf(studentId));
            String studentStr = "";
            for (String sid : set) {
                studentStr += sid + ",";
            }
            studentStr = studentStr.substring(0, studentStr.length() - 1);
            classesCache.put(className, studentStr);
        }

        log.debug("unbinding end:" + cacheKey + " " + classesCache);

    }


    @RequestMapping("/loadCacheClasses")
    @ResponseBody
    public Map<String, String> loadCacheClasses(Long id, HttpSession session) {
        String cacheKey = "classesCache" + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        return classesCache;
    }

    @RequestMapping("/loadCacheStudents")
    @ResponseBody
    public String loadCacheStudents(Long id, String className, HttpSession session) {
        String cacheKey = "classesCache" + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        return classesCache == null ? "" : classesCache.get(className);
    }

    @RequestMapping("/refClassGrid.html")
    public String toRefClassGrid(Long id, HttpSession session, Model model) {
        String cacheKey = "classesCache" + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);

        List<Map> refClasses = loadRefClasses(classesCache);

        model.addAttribute("refClasses", refClasses);

        return "exam/fill::#refClassGrid";
    }

    //班级信息展示处理，将缓存的班级信息转换成可以展示的班级信息（名称，总人数，关联人数）
    private List<Map> loadRefClasses(Map<String, String> classesCache) {
        //先将所有缓存的班级变成一个字符串，利用逗号分隔
        //cs = "xxx-1班,xxx-2班,...."
        //sql = "select * from table where #{cs} like "xxx-1班"

        String classNames = "";
        Set<String> classNameSet = classesCache.keySet();
        for (String className : classNameSet) {
            classNames += className + ",";
        }
        if (classNames.length() == 0) {
            //没有任何缓存数据
            return null;
        }
        classNames = classNames.substring(0, classNames.length() - 1);
        List<Map> refClasses = studentService.findClassesByNames(classNames);

        //此时refClasses中只有2个字段 className，total。
        //但在前端展示时，还需要选择人数，这个信息就在缓存中。
        //还需要从缓存中获取这个选择人数的信息，并将其加入refClasses中
        for (Map refClass : refClasses) {
            String className = (String) refClass.get("className");
            String info = classesCache.get(className);
            refClass.put("custom", "N");
            if (info.equals("ALL")) {
                //全选， 选择人数=总人数
                refClass.put("refTotal", refClass.get("total"));
            } else {
                //只选了部分学生，这些学号用逗号连接
                refClass.put("refTotal", info.split(",").length);
            }
        }

        //上述处理的都是在数据库中存在的班级
        //考试信息关联的班级中，还包括自定义的班级（数据库-student中不存在）
        //这一部分班级也需要处理，并且班级的总人数和关联人数相同
        for (String className : classNameSet) {
            String info = classesCache.get(className);
            if (info.startsWith("x")) {
                //这是一个自定义班级，需要手动处理。
                //经过上诉操作，这个班级并没有被存入refClasses
                Map map = new HashMap();
                map.put("className", className);
                map.put("custom", "Y");
                String[] array = info.split(",");
                map.put("total", array.length - 1);
                map.put("refTotal", array.length - 1);
                refClasses.add(map);
            }
        }

        return refClasses;
    }


    @RequestMapping("/createClass.html")
    public String toCreateClass() {
        return "exam/createClass";
    }

    @RequestMapping("/createClass")
    @ResponseBody
    public boolean createClass(Long id, String className, HttpSession session) {
        String cacheKey = cacheKey_prefix + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        if (classesCache.get(className) != null) {
            return false;
        }

        //x,1,2,3,4,5
        classesCache.put(className, "x");
        return true;
    }

    @RequestMapping(value = "/importClasses", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String importClasses(Long id, MultipartFile excel, HttpSession session) throws IOException {
        String cacheKey = cacheKey_prefix + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);

        String msg = "";
        int count1 = 0;
        int count2 = 0;

        InputStream is = excel.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(is);

        reader.addHeaderAlias("学号", "code");
        reader.addHeaderAlias("姓名", "sname");

        List<String> sheetNames = reader.getSheetNames();
        for (int i = 1; i < sheetNames.size(); i++) {
            String sheetName = sheetNames.get(i);
            //默认读取第一个sheet表。
            reader.setSheet(sheetName);

            List<Student> studentList = reader.readAll(Student.class);
            //此时读取了一个班级的学生信息
            //info存储班级学生的编号字符串
            String info = "";
            List<Student> existStudent = studentService.findExistStudent(studentList);
            for (Student student : existStudent) {
                info += student.getId() + ",";
            }
            info = info.substring(0, info.length() - 1);


            //判断当前班级是否存在。如果存在就正常拼装info，如果不存在，说明是一个自定义班级，需要以x开头
            if (!studentService.isExistClass(sheetName)) {
                info = "x," + info;
            }

            //不考虑缓存中已有该班级情况，如果存在，直接覆盖，以导入为主。
            classesCache.put(sheetName, info);

            //处理反馈问题。 处理存在和不存在学生
            for (Student student : studentList) {
                //循环的是导入的学生（存在，保存在）
                if (existStudent.contains(student)) {
                    //list集合的contains方法底层用equals比较是否相等。
                    //student默认equals比较地址。
                    //我们认为code和sname相等的就是相等。需要重写equals方法
                    count1++;
                } else {
                    msg += "【" + student.getCode() + "-" + student.getSname() + "】存储失败" + "|";
                    count2++;
                }
            }

        }

        msg = "共导入【" + (count1 + count2) + "】学生|成功导入【" + count1 + "】学生|失败导入【" + count2 + "】学生|" + msg;

        return msg;
    }

    @RequestMapping("/removeRefClass")
    @ResponseBody
    public void removeRefClass(Long id, String className, HttpSession session) {
        String cacheKey = cacheKey_prefix + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        classesCache.remove(className);
    }

    @RequestMapping("/downStudentTemplate")
    public ResponseEntity<byte[]> downTemplate() throws IOException {
        return super.downTemplateEXCLX("files/students2.xlsx", "students2.xlsx");
    }

    @RequestMapping(value = "/importStudents", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String importStudents(Long id, String className, MultipartFile excel, HttpSession session) throws IOException {
        String cacheKey = cacheKey_prefix + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);

        String msg = "";
        int count1 = 0;
        int count2 = 0;

        InputStream is = excel.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(is);

        reader.addHeaderAlias("学号", "code");
        reader.addHeaderAlias("姓名", "sname");

        List<String> sheetNames = reader.getSheetNames();

        String sheetName = sheetNames.get(1);
        //默认读取第一个sheet表。
        reader.setSheet(sheetName);

        List<Student> studentList = reader.readAll(Student.class);
        //此时读取了一个班级的学生信息
        //info存储班级学生的编号字符串
        String info = classesCache.get(className);
        //此时追加后，info中可能会有重复的数据 info="1,2,4,5,1"
        //set可以自动去重。  info中的一堆id 装入set（自动去重），再重新组成info
        Set<String> idSet = new HashSet(Arrays.asList(info.split(",")));
        List<Student> existStudent = studentService.findExistStudent(studentList);
        for (Student student : existStudent) {
            String sid = student.getId() + "";
            if (idSet.contains(sid)) {
                continue;
            }
            info += sid + ",";
            idSet.add(sid); //防止后面的数据与当前这个新数据重复。
        }
        info = info.substring(0, info.length() - 1);

        //不考虑缓存中已有该班级情况，如果存在，直接覆盖，以导入为主。
        classesCache.put(className, info);

        //处理反馈问题。 处理存在和不存在学生
        for (Student student : studentList) {
            //循环的是导入的学生（存在，保存在）
            if (existStudent.contains(student)) {
                //list集合的contains方法底层用equals比较是否相等。
                //student默认equals比较地址。
                //我们认为code和sname相等的就是相等。需要重写equals方法
                count1++;
            } else {
                msg += "【" + student.getCode() + "-" + student.getSname() + "】存储失败" + "|";
                count2++;
            }
        }


        msg = "共导入【" + (count1 + count2) + "】学生|成功导入【" + count1 + "】学生|失败导入【" + count2 + "】学生|" + msg;

        return msg;
    }

    @RequestMapping("/adjustStudents.html")
    public String toAdjustStudents(Long id, String className, HttpSession session, Model model) {
        //查出选中的学生信息
        //如果是存在的班级，还需要查出未选中的学生信息
        //如果是自定义班级，初次不查询。未来会通过查询按钮来查询 （service操作相同）

        List<Map<String, Object>> bindStudents;
        List<Map<String, Object>> unbindStudents;
        String custom = "";

        String cacheKey = cacheKey_prefix + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        String info = classesCache.get(className);

        String[] sidArray;
        if (info.startsWith("x")) {
            //"x"-->""
            //"x,1,2,3"-->",1,2,3"
            if (info.length() == 1) {
                //"x"
                sidArray = info.replace("x", "").split(",");
            } else {
                //x,1,2,3,4
                sidArray = info.replace("x,", "").split(",");
            }

        } else {
            //已存在的班级，有2种学生存储情况 id串 和 ALL
            if (info.equals("ALL")) {
                sidArray = new String[]{"ALL"};
            } else {
                sidArray = info.split(",");
            }
        }
        //此时sidArray中存储的就是选中的学生id
        bindStudents = examService.findBindStudents(className, sidArray);

        //处理未关联的学生 （自定义班级不需要默认处理）
        if (info.startsWith("x")) {
            //自定义班级，不做未关联处理
            unbindStudents = new ArrayList<>();
            custom = "Y";
        } else {
            //已选班级，需要将这个班级未选中的学生获取
            if (info.equals("ALL")) {
                unbindStudents = new ArrayList<>();
            } else {
                unbindStudents = examService.findUnbindStudents(className, sidArray);
            }
            custom = "N";
        }

        model.addAttribute("bindStudents", bindStudents);
        model.addAttribute("unbindStudents", unbindStudents);
        model.addAttribute("className", className);
        model.addAttribute("custom", custom);

        return "exam/adjustStudents";
    }

    @RequestMapping("/flushUnbindStudents.html")
    public String toFlushUnbindStudents(Long id, String className, String searchName, HttpSession session, Model model) {
        String cacheKey = cacheKey_prefix + id;
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);
        String info = classesCache.get(className);

        String[] sidArray;
        //目前的逻辑，只有自定义班级才需要额外查询其他班级未绑定的学生信息
        sidArray = info.replace("x,", "").split(",");

        List<Map<String, Object>> unbindStudents = examService.findUnbindStudents(searchName, sidArray);
        model.addAttribute("unbindStudents", unbindStudents);

        return "exam/adjustStudents::#unbindGrid";
    }

    @RequestMapping("/fillSave")
    @ResponseBody
    public Map<String, Object> fillSave(Exam exam, HttpSession session) {
        Map<String, Object> result = null;

        String cacheKey = cacheKey_prefix + exam.getId();
        Map<String, String> classesCache = (Map<String, String>) session.getAttribute(cacheKey);

        //检查缓存中的新数据是否存在重复，并且要给出提示
        result = checkRepeat(classesCache);

        if (result.get("code").equals(1)) {
            //有重复，错误情况，不能保存，直接反馈
            return result;
        }

        examService.fill(exam, classesCache);

        return result;
    }

    /**
     * 检查缓存中是否有重复的学生
     * 如果没有重复的学生，直接给出提示反馈
     * {code:0,msg:'xxxx'}
     * 如果有重复的学生，要知道重复的学生姓名以及在哪个班级中发生了重复，并给出提示反馈
     * {code:1,msg:'【xxx】重复关联，【x班】【y班】'}
     *
     * @param classesCache
     * @return
     */
    private Map<String, Object> checkRepeat(Map<String, String> classesCache) {
        Map<String, Object> result = new HashMap<>();
        //先检查是否有重复
        //再处理那些重复的数据
        List<Set<String>> classList = new ArrayList<Set<String>>();
        Set<String> checkBox = new HashSet<String>();
        int checkCount = 0;

        //装载的就是所有的班级
        String[] classNameArray = new String[classesCache.size()];
        classesCache.keySet().toArray(classNameArray);

        //逐个的将班级中的人员信息取出
        for (String className : classNameArray) {
            // 3种 "ALL","1,2,3,4,5","x,1,2,3,4,5"
            String info = classesCache.get(className);
            if ("ALL".equals(info)) {
                //获取这个班级所有的学生id组成的字符串 ALL->"1,2,3,4,5,6"
                info = examService.findClassAllStudentIds(className);
            } else if (info.startsWith("x")) {
                //"x,1,2,3" --> "1,2,3"
                info = info.replace("x,", "");
            }

            Set<String> idSet = new HashSet<>(Arrays.asList(info.split(",")));
            classList.add(idSet);

            checkBox.addAll(idSet);
            checkCount += idSet.size();
        }
        //至此循环结束，就将所有的缓存的学生信息获取
        //既装入了classList中，方便后面从中寻找重复的数据
        //又将其装入了checkbox，以便检测是否存在重复
        if (checkBox.size() == checkCount) {
            //不存在重复
            result.put("code", 0);
            result.put("msg", "保存成功");
            return result;
        }

        //代码至此，证明存在重复，需要处理获得哪些重复的学生以及所在的班级
        Map<String, String> repeatInfo = new HashMap<>();

        //从头到尾，循环每一个班级
        //最后一个班级不需要处理
        for (int i = 0; i < classList.size() - 1; i++) {
            Set<String> currSidSet = classList.get(i);

            for (int j = i + 1; j < classList.size(); j++) {
                Set<String> targetSidSet = classList.get(j);
                checkBox.clear();
                checkBox.addAll(currSidSet);
                checkBox.addAll(targetSidSet);
                if (checkBox.size() == currSidSet.size() + targetSidSet.size()) {
                    //i班和j班没有重复，i班再和j+1班比较
                    continue; //j++
                }
                //代码至此证明i班和j班有重复的数据
                for (String currSid : currSidSet) {
                    if (targetSidSet.contains(currSid)) {
                        //i班和j班都存在currSid
                        //info="【2020-软件-1班】【2020-软件-2班】"
                        String info = repeatInfo.get(currSid);
                        info = info == null ? "" : info;
                        String iClassName = classNameArray[i];
                        String jClassName = classNameArray[j];
                        if (!info.contains(iClassName)) {
                            info += "【" + iClassName + "】";
                        }
                        if (!info.contains(jClassName)) {
                            info += "【" + jClassName + "】";
                        }
                        repeatInfo.put(currSid, info);
                    }
                }
            }
        }

        //至此重复数据装载完毕
        log.info("repeatInfo : {}", repeatInfo);
        //接下来就是需要将学生id 变成对应的学生姓名，并拼成前端所需要的结果
        String[] repeatSidArray = repeatInfo.keySet().toArray(new String[]{});
        List<Map<String, Object>> repeatStudents = examService.findBindStudents(null, repeatSidArray);

        StringBuilder msg = new StringBuilder();
        for (Map<String, Object> student : repeatStudents) {
            String sid = student.get("sid").toString();
            String sname = student.get("sname").toString();
            String info = repeatInfo.get(sid);
            info = "【" + sname + "】重复关联" + info;
            msg.append(info).append("|");
        }

        result.put("code", 1);
        result.put("msg", msg.toString());

        return result;
    }

    @RequestMapping("/generatePage")
    @ResponseBody
    public boolean generatePage(Long id) {
        if (examService.isPageExist(id)) {
            //存在试卷，此次生成试卷失败。
            return false;
        }

        examService.generatePage(id);

        return true;
    }

    @RequestMapping("/removePage")
    @ResponseBody
    public boolean removePage(Long id) {
        return examService.removePage(id);
    }

    @RequestMapping("/removeExam")
    @ResponseBody
    public boolean removeExam(Long id) {
        return examService.removeExam(id);
    }

    @RequestMapping("/releaseExam")
    @ResponseBody
    public boolean releaseExam(Long id) {
        if (!examService.isPageExist(id)) {
            //不存在试卷，不能发布考试
            return false;
        }

        examService.releaseExam(id);

        return true;
    }

    @RequestMapping("/leaveExam")
    @ResponseBody
    public void leaveExam(Long id) {
        examService.leaveExam(id);
    }

    @RequestMapping("finishExam")
    @ResponseBody
    public boolean finishExam(Long id) {
        Exam exam = examService.findById(id);
        if (exam.getDuration() == null || "".equals(exam.getDuration())) {
            //是一个区间考试
            log.debug("这个考试设置了时间区间，不能手动结束");
            return false;
        }

        examService.finishExam(id);

        return true;
    }

    @RequestMapping("/pageList.html")
    public String toPageList(Long id, Model model) {
        List<Map> classes = examService.findClassesByExam(id);
        model.addAttribute("classes", classes);
        model.addAttribute("examId", id);
        Exam exam = examService.findById(id);
        model.addAttribute("exam", exam);
        return "exam/pageList";
    }

    @RequestMapping("/studentPageList.html")
    public String toStudentPageList(Long examId, String className, Model model) {
        List<StudentExamDTO> students = examService.findStudentsByExamAndClass(examId, className);
        model.addAttribute("students", students);
        return "exam/pageList::#studentGrid";
    }

    @RequestMapping("/changeStudentStatus")
    @ResponseBody
    public void changeStudentStatus(Long examId, Long studentId, String status) {
        examService.changeStudentStatus(examId, studentId, status);
    }

    @RequestMapping("/page.html")
    public String toPage(Long examId, Long studentId, String sname, Model model) {
        Exam exam = examService.findById(examId);
        //获得考生考试题（题，标准答案）
        StudentExam studentExam = examService.findStudentExamById(studentId, examId);
        String pagePath = studentExam.getPagePath();
        pagePath = CommonData.PAGE_ROOT_PATH + pagePath;
        List<QuestionVO> questionVOS = CommonUtil.readPage(pagePath);

        //se中包含了自定义答案
        //这些答案被存储在5个属性中answer1-answer5
        //需要将其组成在一个集合中，并且与上面questionVOS对应
        StudentExamVO studentExamVO = new StudentExamVO(studentExam);


        //考题装载前，需要对每一道考题初始化得分
        handleQuestionInit(questionVOS, studentExamVO);

        model.addAttribute("questionVOS", questionVOS);
        model.addAttribute("studentExamVO", studentExamVO);
        model.addAttribute("exam", exam);
        model.addAttribute("sname", sname);

        return "exam/page";
    }

    /**
     * 对考卷页面展示的考题做初始化处理
     * 考题的得分计算        questionVO.answerList 和 studentExamVO.answerList + StudentExamVO.review
     * 考题自定义答案的装载   studentVO.answerList
     * 考题批阅信息的装载     studentExamVO.review()
     *
     * @param questionVOS
     * @param studentExamVO
     */
    private void handleQuestionInit(List<QuestionVO> questionVOS, StudentExamVO studentExamVO) {
        //记录考题的索引
        int i = 0;

        //获得并存储主观题的得分
        //从2个review中获得，准备装入一个list集合
        //要明白，这个list集合中存储的分数顺序，和考题中填空题与综合题的顺序一致。
        //从review中获得主观题得分和批阅信息
        // 存储主观题索引
        int j = 0;
        List<Integer> score45 = new ArrayList<>();
        List<String> review45 = new ArrayList<>();
        String review4 = studentExamVO.getStudentExam().getReview4();
        if (review4 != null && !"".equals(review4)) {
            //["5,xxx","6,xxx"]
            String[] array = review4.split(CommonData.SPLIT_SEPARATOR);
            for (String s : array) {
                //s=5,aa,a,a,a,a,,a
                String[] as = s.split(",", 2);
                score45.add(Integer.parseInt(as[0]));
                review45.add(as[1]);
            }
        }
        String review5 = studentExamVO.getStudentExam().getReview5();
        if (review5 != null && !"".equals(review5)) {
            //["5,xxx","6,xxx"]
            String[] array = review5.split(CommonData.SPLIT_SEPARATOR);
            for (String s : array) {
                //s=5,aa,a,a,a,a,,a
                String[] as = s.split(",", 2);
                score45.add(Integer.parseInt(as[0]));
                review45.add(as[1]);
            }
        }


        /**
         * 装载所有的自定义答案，但我们只需要客观题部分来计算考题的最终得分。还缺主观的得分
         */
        List<Object> answerList = studentExamVO.getAnswerList();
        if (answerList.size() == 0) {
            //没有任何答案，基本就是缺考，所有分数为0，此时自定义答案为null，review为null
            return;
        }
        
        loop:
        for (QuestionVO question : questionVOS) {
            String type = question.getType();
            if ("单选题".equals(type) || "判断题".equals(type)) {
                //只有1个答案
                String a1 = question.getAnswerList().get(0);
                String a2 = answerList.get(i).toString();
                if (!a2.equals("-1")) {
                    //有自定义答案  null ， list
                    List<String> list = Arrays.asList(a2);
                    question.setEndAnswerList(list);
//                    List<String> list = new ArrayList<>();
//                    list.add(a2);
//                    question.setEndAnswerList(list);
                }


                if (a1.equals(a2)) {
                    //正确
                    question.setEndScore(question.getScore());
                }
            }

            if ("多选题".equals(type)) {
                //只有1个答案
                List<String> a1 = question.getAnswerList(); //["1","2"]
                Object ta2 = answerList.get(i);
                if (ta2.toString().equals("-1")) {
                    //这道多选题没有选择答案
                    i++;
                    continue;
                }
                //选了答案，需要判断答案对不对
                List<Integer> a2 = (List) ta2;//[1,2]
                //存储自定义答案
                List<String> endAnswerList = new ArrayList();
                for (Integer a : a2) {
                    endAnswerList.add(a.toString());
                }
                question.setEndAnswerList(endAnswerList);


                //a1 和 a2 中存储的答案（选项序号），一定都是从小到大排列的。
                if (a1.size() != a2.size()) {
                    //答案数量不对
                    i++;
                    continue;
                }

                //答案数量对上了，内容需要逐一比较
                for (int index = 0; index < a1.size(); index++) {
                    if (!a1.get(index).equals(a2.get(index).toString())) {
                        //有一个答案不一样，此题不对
                        i++;
                        continue loop;
                    }
                }

                //代码至此，有答案，与正确答案数量一致，与正确答案内容一致
                question.setEndScore(question.getScore());

            }

            if ("填空题".equals(question.getType()) || "综合题".equals(question.getType())) {
                //虽然填空题和综合题的得分来自于review记载（score45）
                //但在页面上需要展示学生答案。学生的自定义答案也需要
                if ("填空题".equals(question.getType())) {
                    question.setEndAnswerList((List) answerList.get(i));
                } else {
                    List<String> list = Arrays.asList(answerList.get(i).toString());
                    question.setEndAnswerList(list);
                }

                //需要从review中获得得分
                //注意：第一次批阅时，填空题和综合题还没有更新过分数
                //     所以是null,所以score45.size=0
                if (score45.size() == 0) {
                    //此时还没有填空题和综合题得分
                    question.setEndScore(0);
                } else {
                    question.setEndScore(score45.get(j));
                    question.setReview(review45.get(j));
                    j++;
                }
            }
            i++;
        }
    }


    @RequestMapping("/review")
    @ResponseBody
    public void review(StudentExam studentExam) {
        examService.review(studentExam);
    }

    @RequestMapping("/submit")
    @ResponseBody
    public void submit(Long examId) {
        examService.submit(examId);
    }

    private <T> T getSessionValue(HttpSession session, String sessionKey) {
        return (T) session.getAttribute(sessionKey);
    }
}
