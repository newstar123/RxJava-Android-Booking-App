package app.delivering.mvp.bars.list.init.model;

import java.util.List;

import app.core.bars.list.get.entity.BarListModel;

public class SortedBarListModel {

    private List<BarListModel> models;
    private Boolean haveArrowsBeenShowed;
    private boolean isOpenedTabExisting;

    public SortedBarListModel(List<BarListModel> models, Boolean hasArrowsBeenShowed, boolean isOpenedTabsExist) {

        this.models = models;
        this.haveArrowsBeenShowed = hasArrowsBeenShowed;
        this.isOpenedTabExisting = isOpenedTabsExist;
    }

    public List<BarListModel> getVenueList() {
        return models;
    }

    public Boolean haveArrowsBeenShowed() {
        return haveArrowsBeenShowed;
    }

    public boolean isOpenedTabExisting() {
        return isOpenedTabExisting;
    }
}
