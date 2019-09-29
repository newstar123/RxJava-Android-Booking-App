package app.core.bars.locations.entity.event;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;

public class LocationsModelEvent {
    private List<LocationsModel> model;

    public LocationsModelEvent(List<LocationsModel> model) {
        this.model = model;
    }

    public List<LocationsModel> getModel() {
        return this.model;
    }
}
