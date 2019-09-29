package app.delivering.mvp.bars.detail.init.tablist.list.hours.model;

import app.delivering.mvp.bars.work.model.BarWorkTypeModel;

public class BarDetailWorkHoursModel {
    private String day;
    private String time;
    private boolean isToday;
    private BarWorkTypeModel workTypeInformation;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public void setWorkType(BarWorkTypeModel workTypeInformation) {
        this.workTypeInformation = workTypeInformation;
    }

    public BarWorkTypeModel getWorkTypeInformation() {
        return workTypeInformation;
    }
}
