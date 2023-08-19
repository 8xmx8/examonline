package com.yjy.examonline.test;

import org.junit.Test;

import java.io.File;

public class Test_CreateFile {
    @Test
    public void createFile() {
        String path = "d:" + File.separator + "abc" + File.separator + "abc.txt";
        File file = new File(path);
        if (!file.exists()) file.mkdir();
    }
}
