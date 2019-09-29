package app.delivering.mvp.bars.detail.init.tablist.height.model;

import java.util.List;

import app.delivering.mvp.bars.work.model.BarWorkTypeModel;

public class BarDetailHoursModel {
    private List<String> list;
    private BarWorkTypeModel workTypeInformation;

    public BarDetailHoursModel(List<String> list, BarWorkTypeModel workTypeInformation) {this.list = list;
        this.workTypeInformation = workTypeInformation;
    }

    public List<String> getList() {
        return list;
    }

    public BarWorkTypeModel getWorkTypeInformation() {
        return workTypeInformation;
    }
}
