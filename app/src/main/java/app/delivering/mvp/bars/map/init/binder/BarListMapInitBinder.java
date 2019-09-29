package app.delivering.mvp.bars.map.init.binder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.core.bars.list.get.entity.BarListModel;
import app.delivering.component.bar.map.BarMapFragment;
import app.delivering.component.bar.map.cluster.VendorMarker;
import app.delivering.component.bar.map.cluster.VendorMarkerRenderer;
import app.delivering.component.bar.map.cluster.VendorsAlgorithm;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.map.init.model.BarListMapModel;
import app.delivering.mvp.bars.map.init.presenter.BarListMapInitPresenter;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarListMapInitBinder extends BaseBinder {
    public static final String SEPARATOR = "/";
    @BindView(R.id.map_view) MapView mapView;
    private BarListMapInitPresenter presenter;
    private final InitExceptionHandler initExceptionHandler;
    private ClusterManager<VendorMarker> clusterManager;
    private ArrayList<Marker> markers;
    private List<BarListModel> models;
    private BarMapFragment fragment;

    public BarListMapInitBinder(BarMapFragment fragment) {
        super(fragment.getBaseActivity());
        this.presenter = new BarListMapInitPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        markers = new ArrayList<>();
        this.fragment = fragment;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(CitiesModel cities) {
        showProgress();
        Observable.zip(presenter.process(cities), presenter.process(new CitiesModel()), MapObservable.get(mapView), (barListModels, fullList, map) -> {
            BarListMapModel mapModel = new BarListMapModel();
            mapModel.setSelectedList(barListModels);
            mapModel.setFullList(fullList);
            mapModel.setMap(map);
            mapModel.setSelectCityName(cities.getSelectCityName());
            return mapModel;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fragment.bindUntilEvent(FragmentEvent.STOP))
                .subscribe(this::show, this::onError);
    }

    private void show(BarListMapModel mapModel) {
        this.models = mapModel.getSelectedList();
        clearOldMarkers();
        mapModel.getMap().setOnMapLoadedCallback(() -> moveMap(mapModel.getMap(), mapModel.getSelectCityName()));
        addClusters(mapModel.getMap(), mapModel.getFullList());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapModel.getMap().setMyLocationEnabled(true);
            mapModel.getMap().getUiSettings().setMyLocationButtonEnabled(false);
        }
        hideProgress();
    }

    private void clearOldMarkers() {
        for (Marker marker : markers)
            marker.remove();
        markers.clear();
    }

    private void addClusters(GoogleMap map, List<BarListModel> models) {
        if (clusterManager != null) {
            clusterManager.clearItems();
            map.clear();
        }
        clusterManager = new ClusterManager<>(getActivity(), map);
        VendorMarkerRenderer renderer = new VendorMarkerRenderer(getActivity(), map, clusterManager, models);
        VendorsAlgorithm algorithm = new VendorsAlgorithm();
        clusterManager.setAlgorithm(algorithm);
        clusterManager.setRenderer(renderer);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(renderer);
        addItems(models);
        clusterManager.cluster();
    }

    private void addItems(List<BarListModel> models) {
        for (BarListModel model : models) {
            LatLng latLng = new LatLng(model.getLatitude(), model.getLongitude());
            String name = model.getName();
            String title = model.getId() + SEPARATOR + model.getRouting() + SEPARATOR + model.getDistKm();
            VendorMarker marker = new VendorMarker(latLng, title, name, model.getBarWorkTimeType());
            clusterManager.addItem(marker);
        }
    }

    private void moveMap(GoogleMap map, String selectCityName) {
        if (!TextUtils.isEmpty(selectCityName) && !models.isEmpty()) {
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            for (BarListModel model : models) {
                LatLng latLng = new LatLng(model.getLatitude(), model.getLongitude());
                bounds.include(latLng);
            }
            int sidePadding = getActivity().getResources().getDimensionPixelSize(R.dimen.dip44);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), sidePadding));
            map.setPadding(sidePadding, getActivity().getResources().getDimensionPixelSize(R.dimen.dip60),
                    sidePadding, 0);
        }
    }

    private void onError(Throwable throwable) {
        hideProgress();
        initExceptionHandler.showError(throwable, view -> loadList(new CitiesModel()));
    }
}
