package app.delivering.component.bar.map.cluster;

import android.text.TextUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.List;

import app.R;
import app.core.bars.list.get.entity.BarListModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;
import app.delivering.mvp.bars.list.item.click.events.OnBarItemClickEvent;
import app.delivering.mvp.bars.map.init.binder.BarListMapInitBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;

public class VendorMarkerRenderer extends DefaultClusterRenderer<VendorMarker> implements
        ClusterManager.OnClusterItemClickListener<VendorMarker> {
    private final TitledMarkerCreator markerCreator;
    private final int colorClosed;
    private final int colorClosesSoon;
    private final int colorOpened;
    private List<BarListModel> models;

    public VendorMarkerRenderer(BaseActivity context, GoogleMap map, ClusterManager<VendorMarker> clusterManager, List<BarListModel> models) {
        super(context, map, clusterManager);
        this.models = models;
        colorClosed = context.getResources().getColor(R.color.color_5b606f);
        colorClosesSoon = context.getResources().getColor(R.color.color_ab68e2);
        colorOpened = context.getResources().getColor(R.color.accent);
        markerCreator = new TitledMarkerCreator(context, R.layout.map_marker_icon, R.id.map_marker_title);

    }

    @Override
    protected void onBeforeClusterItemRendered(VendorMarker vendorMarker, MarkerOptions markerOptions) {
        if (vendorMarker.getWorkTime() == BarByWorkTime.CLOSED)
            markerOptions.icon(markerCreator.getMarkerBitmap(vendorMarker.getName(), colorClosed));
        else if (vendorMarker.getWorkTime() == BarByWorkTime.CLOSES_SOON)
            markerOptions.icon(markerCreator.getMarkerBitmap(vendorMarker.getName(), colorClosesSoon));
        else
            markerOptions.icon(markerCreator.getMarkerBitmap(vendorMarker.getName(), colorOpened));
        markerOptions.anchor(0.5f, 1f);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<VendorMarker> cluster, MarkerOptions markerOptions) {
        markerOptions.icon(markerCreator.getMarkerBitmap(String.valueOf(cluster.getSize()), getClusterColor(cluster.getItems())))
                .anchor(0.5f, 1f);
    }

    private int getClusterColor(Collection<VendorMarker> items) {
        int color = colorClosed;
        for (VendorMarker marker : items) {
            if (marker.getWorkTime() == BarByWorkTime.CLOSES_SOON && color != colorOpened)
                color = colorClosesSoon;
            if (marker.getWorkTime() == BarByWorkTime.OPEN)
                color = colorOpened;
        }
        return color;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }

    @Override public boolean onClusterItemClick(VendorMarker vendorMarker) {
        if (!TextUtils.isEmpty(vendorMarker.getTitle()))
            return parseClickEvent(vendorMarker);
        return false;
    }

    private boolean parseClickEvent(VendorMarker vendorMarker) {
        String[] titleItems = vendorMarker.getTitle().split(BarListMapInitBinder.SEPARATOR);
        OnBarItemClickEvent clickEvent = new OnBarItemClickEvent(BarListFilterType.NAME);
        if (titleItems.length > 0) {
            long id = Long.parseLong(titleItems[0]);
            clickEvent.setBarId(id);
            clickEvent.setBarWorkType(getWorkType(id));
        }
        if (titleItems.length > 1)
            clickEvent.setDistance(titleItems[1]);
        if (titleItems.length > 2)
            clickEvent.setDistanceKm(Double.parseDouble(titleItems[2]));

        clickEvent.setNameValue(vendorMarker.getName());
        clickEvent.setClickFromMap(true);
        EventBus.getDefault().post(clickEvent);
        return true;
    }

    private BarByWorkTime getWorkType(long id) {
        for (BarListModel model : models) {
            if (model.getId() == id) {
                sendAnalytics(model);
                return model.getBarWorkTimeType();
            }
        }
        return BarByWorkTime.OPEN;
    }

    private void sendAnalytics(BarListModel barModel) {
        MixpanelSendGateway.send(MixpanelEvents.getBarSelectedEvent(barModel.getName(),
                barModel.getDiscountText(),
                barModel.getCity(),
                barModel.getNeighborhood(),
                "no filters",
                MixpanelEvents.MAP))
                .subscribe(res -> {}, e -> {}, () -> {});
    }

}
