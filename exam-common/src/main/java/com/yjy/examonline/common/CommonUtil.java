package com.yjy.examonline.common;

import com.github.pagehelper.PageInfo;
import com.yjy.examonline.domain.Question;
import com.yjy.examonline.domain.vo.PageVO;
import com.yjy.examonline.domain.vo.QuestionVO;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class CommonUtil {

    private CommonUtil() {
    }

    /**
     * 分页转换器，将mybatis分页插件中的pageInfo转换成前端回显所需要的PageVO
     *
     * @return
     */
    public static PageVO pageCast(PageInfo info, Map condition) {
        return new PageVO(
                info.getPageNum(),
                info.getPageSize(),
                info.getTotal(),
                info.getNavigateLastPage(),
                (int) info.getStartRow(),
                (int) info.getEndRow(),
                info.getList(),
                condition
        );
    }

    public static QuestionVO questionCast(Question question, int index) {
        QuestionVO questionVO = new QuestionVO();
        //原来cache中有10条记录，当前这个就是第11个，序号也应该是11，恰好是cache.size()
        questionVO.setIndex(index);
        questionVO.setSubject(question.getSubject());
        questionVO.setType(question.getType());

        String[] optionArray = question.getOptions().split(CommonData.SPLIT_SEPARATOR);
        questionVO.setOptionList(Arrays.asList(optionArray));

        String[] answerArray = question.getAnswer().split(CommonData.SPLIT_SEPARATOR);
        questionVO.setAnswerList(Arrays.asList(answerArray));

        return questionVO;
    }


    public static String getSystemDateString(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date());
    }

    public static List<QuestionVO> readPage(String pagePath) {
        List<QuestionVO> questions = new ArrayList<>();
        try {

            FileReader r = new FileReader(pagePath);
            StringBuilder content = new StringBuilder();
            char[] cs = new char[0x100];
            int length;
            while ((length = r.read(cs)) != -1) {
                content.append(new String(cs, 0, length));
            }


            String[] array = content.toString().split(CommonData.QUESTION_OPTION_SEPARATOR);


            int index = 0;
            int no = 1;//题号
            QuestionVO question = null;
            for (String value : array) {
                if (index == 0) {
                    //一道新的考题
                    question = new QuestionVO();
                    question.setIndex(no++);
                    questions.add(question);
                    question.setType(value);
                } else if (index == 1) {
                    question.setScore(Integer.parseInt(value));
                } else if (index == 2) {
                    question.setSubject(value);
                } else if (index == 3) {
                    question.setOptionList(Arrays.asList(value.split(CommonData.SPLIT_SEPARATOR)));
                } else if (index == 4) {
                    //value是答案
                    question.setAnswerList(Arrays.asList(value.split(CommonData.SPLIT_SEPARATOR)));
                } else if (index == 5) {
                    //分隔符，当前这道题操作完毕
                    index = 0;
                    continue;
                }

                index++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return questions;
    }

}
