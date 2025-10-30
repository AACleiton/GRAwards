package com.example.grawards.VO;

import java.util.ArrayList;
import java.util.List;

public class ReportVO {

    private List<ReportElementVO> min = new ArrayList<>();
    private List<ReportElementVO> max = new ArrayList<>();

    public ReportVO() {
    }

    public ReportVO(List<ReportElementVO> min, List<ReportElementVO> max) {
        this.min = min;
        this.max = max;
    }

    public List<ReportElementVO> getMin() {
        return min;
    }

    public void setMin(List<ReportElementVO> min) {
        this.min = min;
    }

    public List<ReportElementVO> getMax() {
        return max;
    }

    public void setMax(List<ReportElementVO> max) {
        this.max = max;
    }
}
