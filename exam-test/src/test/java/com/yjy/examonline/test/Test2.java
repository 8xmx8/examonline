package com.yjy.examonline.test;

import com.yjy.examonline.common.CommonData;
import org.junit.Test;

/**
 * 转义字符
 */
public class Test2 {
    @Test
    public void t2() {
        String place = CommonData.SEPARATOR;
        String s = "a" + place + "b" + place + "c" + place;
        String[] split = s.split("}-\\|-\\{");
        for (String s1 : split) {
            System.out.println(s1);
        }
    }
}
