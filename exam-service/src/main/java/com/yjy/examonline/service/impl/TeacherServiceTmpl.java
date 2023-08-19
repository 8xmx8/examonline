package com.yjy.examonline.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.yjy.examonline.common.CommonData;
import com.yjy.examonline.dao.TeacherMapper;
import com.yjy.examonline.domain.Teacher;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceTmpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    private Logger log = LoggerFactory.getLogger(TeacherServiceTmpl.class);

    @Override
    public void save(Teacher teacher) {
        //生成教室名称对应的助记码
        String mnemonicCode = PinyinUtil.getPinyin(teacher.getTname(), "");
        teacher.setMnemonicCode(mnemonicCode);
        //验证，确保教师名称和助记码不重复
        //数据库配置了，下一步将密码加密md5
        String pass = DigestUtil.md5Hex(teacher.getPass());
        teacher.setPass(pass);
        teacher.setCreateTime(new Date(System.currentTimeMillis()));
        try {
            teacherMapper.insert(teacher);
        } catch (DuplicateKeyException e) {
            //用户名或助记码重复
            log.info("名称或助记码重复 [{}]", teacher.getTname());
            throw e;
        }

    }

    @Override
    public Teacher findByName(String tname) {
        return teacherMapper.findByName(tname);
    }

    @Override
    public void updatePwd(Long id, String pass) {
        teacherMapper.updatePwd(id, pass);
    }

    @Override
    public PageVO find(int page, int rows, String tname) {
        //确保page页面的正确性
        //确保下限
        if (page < 1) {
            page = 1;
        }
        //确保上限
        //需要先获得记录总数，从而算出最大页
        //由于我们是默认查询，分页查询，过滤查询3合一。在查询总数时，需要带过滤条件。
        long total = teacherMapper.total(tname);
        int max = (int) (total % rows == 0 ? total / rows : (total / rows + 1));
        max = Math.max(1, max);
        if (page > max) {
            page = max;
        }

        //根据page计算出mysql分页所需要的条件 start，length
        // 第1页   从0开始    找10
        // 第2页   从10开始   找10
        // 第3页   从20开始
        int start = (page - 1) * rows;
        int length = rows;

        List<Teacher> teacherList = teacherMapper.find(start, length, tname);

        Map<String, Object> condition = new HashMap<>();
        condition.put("tname", tname);
        return new PageVO(page, rows, total, max, start, start + length - 1, teacherList, condition);
    }

    @Override
    public Teacher findById(Long id) {
        return teacherMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Teacher teacher) {
        teacher.setMnemonicCode(PinyinUtil.getPinyin(teacher.getTname(), ""));
        try {
            teacherMapper.updateByPrimaryKeySelective(teacher);
        } catch (DuplicateKeyException e) {
            log.debug("名称或助记码重复[{},{}]", teacher.getTname(), teacher.getMnemonicCode());
            throw e;
        }
    }

    @Override
    public void deleteAll(String ids) {
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            teacherMapper.deleteByPrimaryKey(Long.parseLong(id));
        }
    }

    @Override
    public int delete(Long id) {
        return teacherMapper.deleteByPrimaryKey(id);
    }


    @Override
    public String saves(List<Teacher> teachers) {
        String msg = "";
        int fail = 0;
        int success = 0;
        for (Teacher teacher : teachers) {
            teacher.setPass(CommonData.DEFAULT_PASS);
            try {
                this.save(teacher);
                success++;
            } catch (DuplicateKeyException e) {
                log.info("名称或助记码重复 [{}]", teacher.getTname());
                fail++;
                msg += "【" + teacher.getTname() + "】记录，因为名称重复导致失败|";
            }
        }
        msg = "共导入记录【" + teachers.size() + "】条|" + "成功导入记录【" + success + "】条|" + "失败导入记录【" + fail + "】条|" + msg;

        return msg;
    }

    @Override
    public List<Teacher> findAll() {
        return teacherMapper.findAll();
    }

    @Override
    public List<Teacher> findByShare(Long id) {
        return teacherMapper.findByShareFrom(id);
    }
}
