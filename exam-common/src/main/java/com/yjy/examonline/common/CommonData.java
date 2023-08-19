package com.yjy.examonline.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储公共常量
 */
public interface CommonData {
    int DEFAULT_PACE = 1;
    int DEFAULT_ROWS = 10;
    String PAGE_ROOT_PATH = "d:" + File.separator + "z";
    int DEFAULT_STU_CLASS_ROWS = 4;
    String DEFAULT_PASS = "123";
    /**
     * 下载模板地址的加密处理
     * String path = DigestUtil.md5Hex(String);
     */

    String DOWN_STUDENT_XLSX = "students.xlsx";
    String DOWN_TEACHER_XLSX = "teachers.xlsx";
    String DOWN_QUESTION_XLSX = "questions.xlsx";
    String FILE_DEFAULT = "files/";
    String TEMPLATE_TEACHER_XLSX = FILE_DEFAULT + "teacher.xlsx";

    String TEMPLATE_STUDENT_XLSX = FILE_DEFAULT + "student.xlsx";


    String QUESTION_XLSX = FILE_DEFAULT + "questions.xlsx";

    /**
     * 富文本编辑器请求处理图片，的图片上传和下载地址
     */
    String IMAGES_PATH = "D://images";

    String EXAM_IMAGES = IMAGES_PATH + "//exam-images";

    String DEFAULT_TEMPLATE_STATUS = "私有";

    String DEFAULT_QUESTION_STATUS = DEFAULT_TEMPLATE_STATUS;

    String SEPARATOR = "}-|-{";

    String SPLIT_SEPARATOR = "\\}-\\|-\\{";
    String DEFAULT_QUESTION_SESSION = "questionCache";
    String DEFAULT_TEACHER_SESSION = "loginTeacher";

    String OPTION_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    Map DEFAULT_CONDITION = new HashMap();

    String DEFAULT_EXAM_STATUS = "未发布";

    String DEFAULT_STUDENT_EXAM_STATUS = "未考试";

    String STATIC_PAGE_NAME = "page.txt";

    String QUESTION_OPTION_SEPARATOR = "--\r\n";

    String QUESTION_SEPARATOR = "====";
}
