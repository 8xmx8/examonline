package com.yjy.examonline.test;

import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestReadFile {
    private final static String splitAs = "--\r\n";
    private final static String splitAsTitle = "\\}-\\|-\\{";

    @Test
    public void read() throws IOException {
        String fileName = "D:\\z\\c\\page.txt";
        FileReader reader = new FileReader(fileName);
        StringBuilder content = new StringBuilder();
        char[] cs = new char[0x100];
        int length;
        while ((length = reader.read(cs)) != -1) {
            content.append(new String(cs, 0, length));
        }

        String[] array = content.toString().split(splitAs);

        int index = 0;

        int entitled_id = 1;
        for (String value : array) {
            if (index == 0) {
                //一道新的考题
                System.out.println("\33[33;4m第" + (entitled_id++) + "题\33[m");
                System.out.println(value);
            } else if (index == 1) {
                System.out.println(value);
            } else if (index == 2) {
                System.out.println(value);
            } else if (index == 3) {
                List<String> asList = Arrays.asList(value.split(splitAsTitle));

                System.out.println("\33[35m" + asList + "\33[m");
            } else if (index == 4) {
                //value是答案
                List<String> asList = Arrays.asList(value.split(splitAsTitle));


                System.out.println("\33[35m" + asList + "\33[m");

            } else if (index == 5) {
                //分隔符，当前这道题操作完毕
                index = 0;
                System.out.println();
                continue;
            }


            index++;

        }
    }
}
