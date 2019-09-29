package app.delivering.mvp.bars.detail.init.map.binder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.map.BarDetailFullScreenMapFragment;
import app.delivering.component.bar.map.cluster.TitledMarkerCreator;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.toolbar.video.events.OnStopPlayerEvent;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class BarDetailMapBinder extends BaseBinder {
    private static final String splitSign = ",";
    private static final String regex = "\\d+";
    @BindView(R.id.bar_detail_map_container) MapView mapView;
    @BindView(R.id.bar_detail_slogan) TextView slogan;
    private BarDetailModel detailModel;

    public BarDetailMapBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        this.detailModel = detailModel;
        MapObservable.get(mapView).subscribe(this::showDetailOnMap, onErr -> {});
    }

    private void showDetailOnMap(GoogleMap map) {
        TitledMarkerCreator markerCreator = new TitledMarkerCreator(getActivity(), R.layout.map_marker_icon, R.id.map_marker_title);
        LatLng point = new LatLng(detailModel.getBarModel().getLatitude(), detailModel.getBarModel().getLongitude());
        markerCreator.addMarker(map, point, detailModel.getBarModel().getType().getColor(),
                String.format(getString(R.string.vendor_address_minimal_form),
                        getCorrectAddress(detailModel.getBarModel().getAddress()),
                        String.valueOf(detailModel.getBarModel().getZip())));
        moveCamera(map, point);
        showHint(detailModel);
    }

    private String getCorrectAddress(String address) {
        StringBuilder correctAddress = new StringBuilder();
        if (TextUtils.isEmpty(address)) return correctAddress.toString();
        String[] parts = address.split(splitSign);
        if (parts.length == 1) return address;
        for (String part : parts){
            if (!part.matches(regex))
                correctAddress.append(part);
        }
        return correctAddress.toString();
    }

    private void moveCamera(GoogleMap map, LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    }

    @OnClick(R.id.bar_detail_click_map_container) void onMapClick() {
        if (detailModel != null)
            MapObservable.get(mapView).subscribe(this::startFullScreenMap, onErr -> {});
    }

    @OnLongClick(R.id.bar_detail_click_map_container)
    boolean onMapLongClick() {
        String title = String.format(getString(R.string.vendor_address_minimal_form),
                detailModel.getBarModel().getAddress(),
                detailModel.getBarModel().getCity(),
                detailModel.getBarModel().getState(),
                detailModel.getBarModel().getZip());
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("address", title);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), String.format(getString(R.string.address_value_saved), title), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void startFullScreenMap(GoogleMap map) {
        EventBus.getDefault().post(new OnStopPlayerEvent());
        BarDetailFullScreenMapFragment fragment = new BarDetailFullScreenMapFragment();
        Bundle bundle = new Bundle();
        LatLng latLng = map.getCameraPosition().target;
        bundle.putParcelable(BarDetailFullScreenMapFragment.TARGET_POSITION, latLng);
        bundle.putString(BarDetailFullScreenMapFragment.MARKER_ADDRESS, detailModel.getBarModel().getAddress());
        bundle.putString(BarDetailFullScreenMapFragment.MARKER_CITY, detailModel.getBarModel().getCity());
        bundle.putString(BarDetailFullScreenMapFragment.MARKER_STATE, detailModel.getBarModel().getState());
        bundle.putString(BarDetailFullScreenMapFragment.MARKER_ZIP, String.valueOf(detailModel.getBarModel().getZip()));
        bundle.putString(BarDetailFullScreenMapFragment.MARKER_COLOR, detailModel.getBarModel().getType().getColor());
        fragment.setArguments(bundle);
        getActivity().start(fragment);
    }

    private void showHint(BarDetailModel detailModel) {
        slogan.setText(String.format(getString(R.string.bar_detail_slogan_value_form), detailModel.getBarModel().getSlogan()));
    }

}