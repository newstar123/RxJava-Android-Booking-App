package app.delivering.mvp.bars.map.init.model;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import app.core.bars.list.get.entity.BarListModel;

public class BarListMapModel {
    private List<BarListModel> selectedList;
    private List<BarListModel> fullList;
    private GoogleMap map;
    private String selectCityName;

    public List<BarListModel> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<BarListModel> selectedList) {
        this.selectedList = selectedList;
    }

    public List<BarListModel> getFullList() {
        return fullList;
    }

    public void setFullList(List<BarListModel> fullList) {
        this.fullList = fullList;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public String getSelectCityName() {
        return selectCityName;
    }

    public void setSelectCityName(String selectCityName) {
        this.selectCityName = selectCityName;
    }
}
