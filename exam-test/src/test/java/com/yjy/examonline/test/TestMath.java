package com.yjy.examonline.test;

import org.junit.Test;


public class TestMath {

    @Test
    public void math() {
        double d = Math.pow(Math.sin(5), 2) + Math.pow(Math.cos(5), 2);
        double m = Math.sin(5) * Math.sin(5) + Math.cos(5) * Math.cos(5);
        System.out.println(d + "\n" + m);
    }
}
