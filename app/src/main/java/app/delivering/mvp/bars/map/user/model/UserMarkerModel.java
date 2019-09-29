package app.delivering.mvp.bars.map.user.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class UserMarkerModel {
    private LatLng position;
    private Bitmap marker;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getMarker() {
        return marker;
    }

    public void setMarker(Bitmap marker) {
        this.marker = marker;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
