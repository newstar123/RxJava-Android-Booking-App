package app.delivering.mvp.main.show.model;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;

public class CitiesModel {
    private List<LocationsModel> cities;
    private List<String> spinnerNames;
    private String selectCityName;
    private LocationsModel nearLocation;
    private boolean isManualRefreshing;

    public List<LocationsModel> getCities() {
        return cities;
    }

    public void setCities(List<LocationsModel> cities) {
        this.cities = cities;
    }

    public List<String> getSpinnerNames() {
        return spinnerNames;
    }

    public void setSpinnerNames(List<String> spinnerNames) {
        this.spinnerNames = spinnerNames;
    }

    public String getSelectCityName() {
        return selectCityName;
    }

    public void setSelectCityName(String selectCityName) {
        this.selectCityName = selectCityName;
    }

    public void setNearLocation(LocationsModel nearLocation) {
        this.nearLocation = nearLocation;
    }

    public LocationsModel getNearLocation() {
        return nearLocation;
    }

    public boolean isManualRefreshing() {
        return isManualRefreshing;
    }

    public void setManualRefreshing(boolean manualRefreshing) {
        isManualRefreshing = manualRefreshing;
    }
}
