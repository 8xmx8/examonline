package com.yjy.examonline.controller;


import cn.hutool.crypto.digest.DigestUtil;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.service.TeacherService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;


/**
 * 提供一些基本的功能处理
 */
@Controller
public class CommonController extends BaseControllerAndUpload {


    @Autowired
    private TeacherService teacherService;

    @RequestMapping({"/common/login.html", "/"})
    public String toLogin() {
        return "common/login";
    }

    @RequestMapping("/common/login")
    @ResponseBody
    public boolean checkLogin(String tname, String pass, HttpSession session) {
        log.info(tname);
        log.info(pass);
        //根据用户名找信息在判断密码
        Teacher teacher = teacherService.findByName(tname);
        if (teacher == null) {
            log.info("登录失败-用户名不正确");
            return false;
        }

        pass = DigestUtil.md5Hex(pass);
        if (!teacher.getPass().equals(pass)) {
            log.info("登录失败-密码错误");
            return false;
        }

        log.info("登录成功");
        session.setAttribute("loginTeacher", teacher);
        return true;
    }

    @RequestMapping("/common/main.html")
    public String toMain() {
        return "common/main";
    }


    @RequestMapping("/common/exit")
    public String exit(HttpSession session) {
        //session.invalidate();
        session.removeAttribute("loginTeacher");
        return "common/login";
    }

    @RequestMapping("/common/timeout.html")
    public String toTimeout() {
        return "common/timeout";
    }

    @RequestMapping("common/updatePwdTemplate.html")
    public String toUpdatePwd() {
        return "common/updatePwdTemplate";
    }

    @RequestMapping("common/updatePwd")
    @ResponseBody
    public boolean updatePwd(String old_pass, String new_pass, HttpSession session) {
        log.debug("old_pass [" + old_pass + "]");
        log.debug("new_pass [{}]", new_pass);
        Teacher teacher = (Teacher) session.getAttribute("loginTeacher");
        old_pass = DigestUtil.md5Hex(old_pass);
        if (!teacher.getPass().equals(old_pass)) {
            log.debug("update fail");
            return false;
        }
        new_pass = DigestUtil.md5Hex(new_pass);
        Long tid = teacher.getId();
        teacherService.updatePwd(tid, new_pass);
        log.debug("update success");
        return true;
    }

    /**
     * 存储图片的位置固定
     *
     * @param imgs
     * @return
     * @throws IOException
     */
    @RequestMapping("/common/editor-upload-img")
    @ResponseBody
    public Map<String, Object> editorUploadImg(MultipartFile[] imgs) throws IOException {

        Map<String, Object> results = new HashMap<String, Object>();

        results.put("errno", "0");
        List data = new ArrayList();
        results.put("data", data);

        for (MultipartFile file : imgs) {
            String prefix = UUID.randomUUID().toString().replace("-", "");
            String filename = prefix + "_" + file.getOriginalFilename();
            File isFile = new File(CommonData.IMAGES_PATH);
            if (!isFile.exists()) isFile.mkdir();

            OutputStream os = new FileOutputStream(isFile + filename);
            os.write(file.getBytes());
            os.close();

            Map map = new HashMap();
            map.put("url", "common/editor-img?file=" + filename);
            map.put("alt", "");
            map.put("href", "");

            data.add(map);
        }

        return results;
    }


    @RequestMapping("/common/editor-img")
    public ResponseEntity<byte[]> editorImg(String file) throws IOException {
        InputStream is = new FileInputStream(CommonData.EXAM_IMAGES + file);
        byte[] bytes = IOUtils.toByteArray(is);

        return new ResponseEntity<byte[]>(bytes, new HttpHeaders(), HttpStatus.OK);
    }
}
