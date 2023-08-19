package com.yjy.examonline.service;


import com.yjy.examonline.domain.Template;
import com.yjy.examonline.domain.vo.PageVO;

import java.util.Map;

public interface TemplateService {

    /**
     * @param template 模板名称重复，抛出异常
     * @see #save(Template)
     */
    void save(Template template);

    /**
     * @param page
     * @param rows
     * @param condition { name , course , type , status , tid ,shareId}
     *                  tid:必须存在，当前老师
     *                  shareId:分享老师的id
     * @return
     */
    PageVO find(int page, int rows, Map condition);

    boolean delete(Long id, Long tid);

    void changeStatus(Long id, String status);

    void shareTemplate(Long templateId, Long teacherId);

    Template findById(Long id);
    
    void update(Template template);
}
