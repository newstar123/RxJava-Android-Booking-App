package app.delivering.mvp.bars.work.model;

import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;

public class BarWorkTypeModel {
    private BarByWorkTime barWorkTimeType;
    private String workTypeText;

    public void setBarWorkTimeType(BarByWorkTime barWorkTimeType) {
        this.barWorkTimeType = barWorkTimeType;
    }

    public void setWorkTypeText(String workTypeText) {
        this.workTypeText = workTypeText;
    }

    public BarByWorkTime getBarWorkTimeType() {
        return barWorkTimeType;
    }

    public String getWorkTypeText() {
        return workTypeText;
    }
}
