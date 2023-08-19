package com.yjy.examonline.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class TeacherController extends BaseControllerAndUpload {

    @Autowired
    private TeacherService teacherService;


    @RequestMapping("/teacher/teacher.html")
    public String toTeacher(Model model) {
        //携带数据
        PageVO pageVO = teacherService.find(CommonData.DEFAULT_PACE, CommonData.DEFAULT_ROWS, null);
        model.addAttribute("pageVO", pageVO);
        return "teacher/teacher";
    }

    @RequestMapping("/teacher/pageTemplate.html")
    public String toPageTemplate(int pageNo, String tname, Model model) {
        PageVO pageVO = teacherService.find(pageNo, CommonData.DEFAULT_ROWS, tname);
        model.addAttribute("pageVO", pageVO);
        return "teacher/teacher::#pageTemplate";
    }

    @RequestMapping("teacher/formTemplate.html")
    public String toFormTemplate(Long id, Model model) {
        if (id != null) {
            Teacher teacher = teacherService.findById(id);
            model.addAttribute("teacher", teacher);
        }
        return "teacher/formTemplate";
    }

    @RequestMapping("/teacher/save")
    @ResponseBody
    public boolean save(Teacher teacher) {
        log.debug("tname : [{}]", teacher.getTname());

        teacher.setPass(CommonData.DEFAULT_PASS);
        try {
            teacherService.save(teacher);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @RequestMapping("/teacher/update")
    @ResponseBody
    public boolean update(Teacher teacher) {
        try {
            teacherService.update(teacher);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @RequestMapping("/teacher/deleteAll")
    @ResponseBody
    public void deleteAll(String ids) {
        teacherService.deleteAll(ids);
    }

    @RequestMapping("/teacher/toDeleteOne")
    @ResponseBody
    public boolean deleteOne(Long id) {
        int delete = teacherService.delete(id);
        if (delete == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping("/teacher/importsTemplate.html")
    public String toImports() {
        return "teacher/importsTemplate";
    }

    @RequestMapping("/teacher/downTemplate")
    public ResponseEntity<byte[]> downTemplate() throws IOException {
        return super.downTemplateEXCLX(CommonData.TEMPLATE_TEACHER_XLSX, CommonData.DOWN_TEACHER_XLSX);
    }

    @RequestMapping(value = "/teacher/imports", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String imports(MultipartFile excel) throws IOException {
        //使用POI技术读取excel
        //可以使用POI原生功能读取excel
        //也可以使用hutool工具，内部封装了poi
        //hutool -> POI -> excel.getInputStream()
        ExcelReader reader = ExcelUtil.getReader(excel.getInputStream());
        reader.addHeaderAlias("教师名称", "tname");
        //List<Teacher> teachers = reader.readAll(Teacher.class);
        List<Teacher> teachers = reader.read(1, 2, Teacher.class);
        String msg = teacherService.saves(teachers);
        return msg;
    }

    @RequestMapping("/teacher/exports")
    public ResponseEntity<byte[]> exports() {
        List<Teacher> teachers = teacherService.findAll();

        ExcelWriter writer = ExcelUtil.getWriter(true);

        writer.addHeaderAlias("id", "教师编号");
        writer.addHeaderAlias("tname", "教师名称");
        writer.addHeaderAlias("mnemonicCode", "助记码");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("updateTime", "修改时间");


        //设置只输出有别名字段
        writer.setOnlyAlias(true);

        writer.write(teachers);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        writer.flush();
        writer.close();
        byte[] bs = os.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "attachment;filename=teachers.xlsx");
        headers.add("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<byte[]>(bs, headers, HttpStatus.OK);
    }

}
