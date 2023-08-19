package com.yjy.examonline.test;

import com.yjy.examonline.common.CommonData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class TestIOInPut {

    public InputStream t3(String path) {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }

    @Test
    public void print() throws IOException {
        InputStream stream = t3(CommonData.QUESTION_XLSX);
        System.out.println(stream.available());
    }
}
