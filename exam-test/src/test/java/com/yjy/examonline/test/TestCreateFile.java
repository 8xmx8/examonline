package com.yjy.examonline.test;

import org.junit.Test;

import java.io.File;

public class TestCreateFile {
    @Test
    public void createFiles() {

        String fileName1 = "D:\\images\\a\\bcd.docx";
        File file = new File(fileName1);
//        if (file.isFile()) System.out.println(123);
        String absoluteFile = file.getAbsolutePath();
//
        // System.out.println(absoluteFile);

        if (!file.exists()) {

//            String[] split = fileName1.split("(.*\\\\\\\\)(.*)", 2);
//            for (String s : split) {
//                System.out.println(s);
//            }
            String[] strings = absoluteFile.split("(?=((\\w*)\\.docx))", 2);
            for (String string : strings) {
                System.out.println(string);
            }
            System.out.println("创建文件成功");
        }

//        String filePath = "D:\\images\\a\\bcd.docx";
//        String pattern = "(.*\\\\)(.*)";
//
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(filePath);
//
//        if (m.find()) {
//            String path = m.group(1);
//            String fileName = m.group(2);
//
//            System.out.println("路径: " + path);
//            System.out.println("文件名: " + fileName);
//        } else {
//            System.out.println("无法匹配路径和文件名");
//        }
    }

    //  D:\images\a\bcd.docx
    //D:\images\a
    //bcd.docx

}
