package com.yjy.examonline.test;

import org.junit.Test;

public class Test_Math {
    /**
     * 小明 今年 17 岁 ， 妈妈 今年 40 岁 ， 哥哥 的年龄 是 妈妈和小明年龄和的一半 还要 3 岁 <br/>
     */
    @Test
    public void math() {

        double x = 17;
        double y = 40;
        double c = (x + y) / 2 + 3;
        System.out.println(c);
    }
}
