package com.yjy.examonline.test;

import org.junit.Test;

public class TestSubstringIndex {
    @Test
    public void Test3() {
        String questions = "jvm是【】语言的【】，Java语言的3大特性是：【】，【】，【】";

        int i = 0;
        int count = 0;
        while ((i = questions.indexOf("【】")) != -1) {
            count++;
            questions = questions.substring(i + 1);
            System.out.println(questions);
        }
        System.out.println(count);


    }
}
