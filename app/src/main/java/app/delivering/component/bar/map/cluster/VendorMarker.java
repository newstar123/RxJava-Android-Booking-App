package app.delivering.component.bar.map.cluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;

public class VendorMarker implements ClusterItem {
    private final LatLng position;
    private String title;
    private String name;
    private BarByWorkTime workTime;

    public VendorMarker(LatLng position, String title, String name, BarByWorkTime workTime) {
        this.position = position;
        this.title = title;
        this.name = name;
        this.workTime = workTime;
    }

    @Override public LatLng getPosition() {
        return position;
    }

    @Override public String getTitle() {
        return title;
    }

    @Override public String getSnippet() {
        return null;
    }

    public BarByWorkTime getWorkTime() {
        return workTime;
    }

    public String getName() {
        return name;
    }
}
