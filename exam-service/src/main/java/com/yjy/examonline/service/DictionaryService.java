package com.yjy.examonline.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface DictionaryService {

    Logger log = LoggerFactory.getLogger(DictionaryService.class);

    /**
     * 查询所有的专业信息
     *
     * @return
     */
    List<String> findMajors();

    List<String> findCourses();
}
