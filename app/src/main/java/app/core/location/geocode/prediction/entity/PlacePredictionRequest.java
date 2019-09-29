package app.core.location.geocode.prediction.entity;


import android.location.Location;

public class PlacePredictionRequest {
    private String query;
    private Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
