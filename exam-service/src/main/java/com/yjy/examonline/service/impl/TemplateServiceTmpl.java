package com.yjy.examonline.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.common.CommonUtil;
import com.yjy.examonline.dao.QuestionMapper;
import com.yjy.examonline.dao.TeacherMapper;
import com.yjy.examonline.dao.TemplateMapper;
import com.yjy.examonline.domain.Question;
import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.domain.Template;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.domain.vo.TemplateVO;
import com.yjy.examonline.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TemplateServiceTmpl extends BaseLoggerTmpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public void save(Template template) {
        try {
            templateMapper.insert(template);
        } catch (DuplicateKeyException e) {
            log.debug("模板名称重复:[{}]", template.getName());
        }
    }

    @Override
    public PageVO find(int page, int rows, Map condition) {

        PageHelper.startPage(page, rows);
        List<Template> templates = templateMapper.find(condition);
        PageInfo<Template> pageInfo = new PageInfo<>(templates);

        PageVO pageVO = CommonUtil.pageCast(pageInfo, condition);

        List<Template> templateList = (List<Template>) pageVO.getData();
        List<TemplateVO> templateVOList = new ArrayList<>();

        for (Template template : templateList) {
            TemplateVO vo = new TemplateVO();
            vo.setId(template.getId());
            vo.setName(template.getName());
            vo.setQuestion1(template.getQuestion1());
            vo.setQuestion2(template.getQuestion2());
            vo.setQuestion3(template.getQuestion3());
            vo.setQuestion4(template.getQuestion4());
            vo.setQuestion5(template.getQuestion5());
            vo.setStatus(template.getStatus());
            vo.setTid(template.getTid());
            vo.setType(template.getType());
            vo.setYl1(template.getYl1());
            vo.setYl2(template.getYl2());
            vo.setYl3(template.getYl3());
            vo.setYl4(template.getYl4());
            vo.setCreateTime(template.getCreateTime());
            vo.setUpdateTime(template.getUpdateTime());
            vo.setTotalScore(template.getTotalScore());

            Teacher teacher = teacherMapper.selectByPrimaryKey(template.getTid());
            vo.setTeacher(teacher);

            //只有当前老师的模板，才关心分享给了哪些其他老师。
            //如果当前这个模板时其他老师分享给我的，我是不需要知道这个模板还被分享给了哪些其他老师
            Long tid = (Long) condition.get("tid");
            if (tid.equals(template.getTid())) {
                List<Teacher> shareTeachers = teacherMapper.findByShareTo(template.getId());
                vo.setShareTeachers(shareTeachers);
            }

            templateVOList.add(vo);
        }

        pageVO.setData(templateVOList);

        return pageVO;
    }

    @Override
    public boolean delete(Long id, Long tid) {
        int count = templateMapper.delete(id, tid);
        return count > 0;
    }

    @Override
    public void changeStatus(Long id, String status) {
        Template template = new Template();
        template.setId(id);
        template.setStatus(status);
        templateMapper.updateByPrimaryKeySelective(template);

        //增加针对于静态模板公有和丢弃状态设置时，关联考题的设置
        template = templateMapper.selectByPrimaryKey(id);
        if (template.getType().equals("静态模板") && ("公有".equals(status) || "丢弃".equals(status))) {
            //都需要将模板关联的考题设置为公有
            changeQuestionPublicStatus(template.getQuestion1());
            changeQuestionPublicStatus(template.getQuestion2());
            changeQuestionPublicStatus(template.getQuestion3());
            changeQuestionPublicStatus(template.getQuestion4());
            changeQuestionPublicStatus(template.getQuestion5());
        }

    }

    @Override
    public void shareTemplate(Long templateId, Long teacherId) {
        try {
            templateMapper.share(templateId, teacherId);
        } catch (DuplicateKeyException e) {
            log.warn("repeat share");
        }
    }

    @Override
    public Template findById(Long id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Template template) {
        templateMapper.updateByPrimaryKeySelective(template);
    }

    private void changeQuestionPublicStatus(String questionStr) {
        String[] array = questionStr.split(CommonData.SPLIT_SEPARATOR);
        for (int i = 1; i < array.length; i++) {
            String qid = array[i];
            Question question = new Question();
            question.setId(Long.valueOf(qid));
            question.setStatus("公有");
            questionMapper.updateByPrimaryKeySelective(question);
        }
    }

}
