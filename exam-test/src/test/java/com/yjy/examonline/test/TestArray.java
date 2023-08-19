package com.yjy.examonline.test;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestArray {
    @Test
    public void t4() {
        Map<String, String> classesCache = new HashMap<>();
        classesCache.put("1.5", "1");
        classesCache.put("2.5", "2");
        classesCache.put("3.5", "3");
        String[] classNameArray = new String[classesCache.size()];
        classesCache.keySet().toArray(classNameArray);

        for (String s : classNameArray) {
            System.out.println(s);
        }

    }
}
