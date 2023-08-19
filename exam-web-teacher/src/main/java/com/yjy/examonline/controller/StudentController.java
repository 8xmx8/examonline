package com.yjy.examonline.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.domain.Student;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.service.DictionaryService;
import com.yjy.examonline.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController extends BaseControllerAndUpload {
    @Autowired
    private StudentService studentService;

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * list集合存在，数据过大时候在进行保存操作时候 --》时间复杂度大
     * 优化，在数据库中没有把数据唯一  --》数据库唯一，利用map集合 || 二分查找
     */
    private List<String> specializes = new ArrayList<>();


    @RequestMapping("/student.html")
    public String toStudent() {
        return "student/student";
    }


    @RequestMapping("/importsTemplate.html")
    public String toImportsTemplate() {
        return "student/importsTemplate";
    }


    @RequestMapping("/downTemplate")
    public ResponseEntity<byte[]> downTemplate() throws IOException {
        return super.downTemplateEXCLX(CommonData.TEMPLATE_STUDENT_XLSX, CommonData.DOWN_STUDENT_XLSX);
    }

    @RequestMapping(value = "/imports", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String imports(MultipartFile excel) throws IOException {
        InputStream is = excel.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(is);

        List<Student> students = new ArrayList<>();

        reader.addHeaderAlias("学号", "code");
        reader.addHeaderAlias("姓名", "sname");

        List<String> sheetNames = reader.getSheetNames();
        for (int i = 1; i < sheetNames.size(); i++) {
            String sheetName = sheetNames.get(i);
            //默认读取第一个sheet表。
            reader.setSheet(sheetName);

            List<Student> studentList = reader.readAll(Student.class);
            //完善学生信息  助记码（service产生），提供默认密码，提供学年，专业，班级=sheetName
            String[] info = sheetName.split("-");
            int grade = Integer.parseInt(info[0]);
            String major = info[1];
            String classNo = info[2];
            for (Student student : studentList) {
                student.setPass(CommonData.DEFAULT_PASS);
                student.setGrade(grade);
                student.setMajor(major);
                student.setClassNo(classNo);
            }
            //此时当前表格学生信息完善。就可以装入总集合
            students.addAll(studentList);
        }

        log.debug("import size [{}]", students.size());
        String msg = studentService.saves(students);
        return msg;
    }

    @RequestMapping("/classesDefaultTemplate.html")
    public String toDefaultClassTemplate() {
        return "student/classesTemplate::classesTemplate";
    }

    /**
     * @param pageNo
     * @param condition 四个参数，只关心其中三个
     * @return
     */
    @RequestMapping("/classesTemplate.html")
    public String toClassTemplate(int pageNo, @RequestParam Map condition, Model model) {

        log.debug("pageNo [{}]", pageNo);
        log.debug("condition [{}]", condition);

        PageVO pageVO = studentService.find(pageNo, condition);
        model.addAttribute("pageVO", pageVO);
        return "student/classesTemplate::classesTemplate";
    }

    /**
     * @param condition 可能含有 code,sname,className
     *                  className : 2022-软件-1班
     * @return
     */
    @RequestMapping("/studentsTemplate.html")
    public String toStudentsTemplate(@RequestParam Map condition, Model model) {
        List<Student> students = studentService.findStudents(condition);
        model.addAttribute("students", students);
        return "student/studentsTemplate::studentsTemplate";
    }

    @RequestMapping("/deleteByOne")
    @ResponseBody
    public boolean deleteOne(Long id) {
        int delete = studentService.delete(id);
        if (delete == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping("/formTemplate.html")
    public String toFromTemplate(Long id, Model model) {
        //模板页面带有专业信息，要进行查询
        List<String> majors = dictionaryService.findMajors();
        specializes.addAll(majors);

        if (id != null) {
            //编辑处理
            Student student = studentService.findById(id);
            model.addAttribute("student", student);

        }
        model.addAttribute("majors", majors);
        return "student/formTemplate";
    }

    /**
     * 再进行保存操作时候，前端虽然已经限制了专业
     * 确保程序安全性，在后端也进行判断
     *
     * @param specialize
     * @return
     */
    private boolean isInsertByMajor(String specialize) {
        if (specialize != null && !"".equals(specialize)) {
            for (String major : specializes) {
                /**
                 * @see java.lang.String#equals(String)
                 * 后期要把contains改为equals
                 */
                if (major.contains(specialize)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMapping("/save")
    @ResponseBody
    public boolean save(Student student) {
        student.setPass(CommonData.DEFAULT_PASS);
        String major = student.getMajor();
        if (!this.isInsertByMajor(major)) {
            log.info("录入的学生姓名：[{}],没有相关专业[{}]", student.getSname(), student.getMajor());
            return false;
        }
        try {
            studentService.save(student);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public boolean updateStudent(Student student) {
        try {
            studentService.update(student);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @RequestMapping("/deleteClass")
    @ResponseBody
    public void deleteClass(String className) {
        studentService.deleteByClasses(className);
    }

    @RequestMapping("/deleteClasses")
    @ResponseBody
    public void deleteClasses(String classNames) {
        studentService.deleteByClasses(classNames);
    }

    @RequestMapping("/deleteStudents")
    @ResponseBody
    public void deleteStudents(String ids) {
        studentService.deleteStudents(ids);
    }

    @RequestMapping("/exportClasses")
    @ResponseBody
    public ResponseEntity<byte[]> exportClasses(@RequestParam Map condition, HttpServletResponse resp) throws IOException {
        List<Student> students = studentService.findStudentsByClasses(condition);

        //默认向第一个sheet表写入数据
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.addHeaderAlias("code", "学号");
        writer.addHeaderAlias("sname", "姓名");
        writer.addHeaderAlias("mnemonicCode", "助记码");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.setOnlyAlias(true);

        //装载当前班级的学生
        List<Student> currStudents = new ArrayList<>();
        //存储当前班级的名称  即作为sheet表名称，也作为每次的判断条件
        String currClassName = "";
        for (Student student : students) {
            String className = student.getGrade() + "-" + student.getMajor() + "-" + student.getClassNo();
            if (currClassName.equals("")) {
                //当前是第一个班级的学生，接下来的学生应该都是这个班级的，直到遇见不同的班级位置
                currClassName = className;
                currStudents.add(student);
                continue;
            }
            if (className.equals(currClassName)) {
                //当前这个学生，就是我们现在正在处理的班级的学生
                currStudents.add(student);
                continue;
            }

            if (!className.equals(currClassName)) {
                //当前这个学生，不是我们现在正在处理的班级中的学生
                //证明之前的班级学生已经找齐了，就可以保存了
                //先创建一个新的sheet表，名字就是班级名称，将这个班级的学生写入。
                writer.setSheet(currClassName);
                writer.write(currStudents);

                //上一个班级写完了，接下来准备下一个班级了
                //当前这个学生就是下一个班级的学生
                currClassName = className;
                currStudents.clear();
                currStudents.add(student);
            }

        }

        //循环结束后，注意，还有一个班的学生没有做写入处理呢
        writer.setSheet(currClassName);
        writer.write(currStudents);

        //注意，所有班级的学生，都是创建了各自班级的sheet表，并写入的
        //首次创建writer时，也有一个sheet表，没有，需要移除
        writer.getWorkbook().removeSheetAt(0);

        //至此，就将所有的学生，按班级写入了excel（缓存）
        //接下来需将excel数据，写回给浏览器（下载）
        //  直接使用response响应。
        //  利用responseEntity响应。

        resp.addHeader("content-disposition", "attachment;filename=students.xlsx");
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        writer.flush(resp.getOutputStream());
        resp.getOutputStream().flush();
        writer.close();

        return null;
    }

}
