package com.yjy.examonline.domain.vo;

import java.util.List;
import java.util.Map;

/**
 * 装载自定义分页展示数据
 */
public class PageVO {
    private int curr;
    private int rows;
    private long total;
    private long max;
    private long start;

    private long end;
    private List<?> data;
    private Map<String, Object> condition;

    public PageVO(int curr, int rows, long total, long max, long start, long end, List<?> data, Map<String, Object> condition) {
        this.curr = curr;
        this.rows = rows;
        this.total = total;
        this.max = max;
        this.start = start;
        this.end = end;
        this.data = data;
        this.condition = condition;
    }

    public PageVO() {
    }

    public long getStartPage() {
        if (curr > 2 && curr + 2 < max) {
            return curr - 2;
        } else if (curr <= 2) {
            return 1;
        } else {
            //curr + 2>=max
            return max - 5 + 1;
        }
    }

    public long getEndPage() {
        if (curr > 2 && curr + 2 < max) {
            return curr + 2;
        } else if (curr <= 2 && max > 5) {
            return 5;
        } else {
            //curr + 2>=max
            return max;
        }
    }

    public int getCurr() {
        return curr;
    }

    public void setCurr(int curr) {
        this.curr = curr;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }
}
