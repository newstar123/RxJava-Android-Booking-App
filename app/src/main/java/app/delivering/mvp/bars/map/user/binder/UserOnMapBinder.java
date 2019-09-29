package app.delivering.mvp.bars.map.user.binder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.map.user.model.UserMarkerModel;
import app.delivering.mvp.bars.map.user.presenter.UserOnMapPresenter;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class UserOnMapBinder extends BaseBinder {
    private static final float DEFAULT_ZOOM = 12;
    @BindView(R.id.map_view) MapView mapView;
    private UserOnMapPresenter presenter;
    private final InitExceptionHandler initExceptionHandler;

    public UserOnMapBinder(BaseActivity activity) {
        super(activity);
        this.presenter = new UserOnMapPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(CitiesModel cities) {
        Observable.zip(presenter.process(cities.getSelectCityName()), MapObservable.get(mapView), (markerModel, map) -> {
            show(markerModel, map, cities.getSelectCityName());
            return true;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(isSuccess -> {}, this::onError);
    }

    private void show(UserMarkerModel markerModel, GoogleMap map, String selectCityName) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                map.setMyLocationEnabled(true);
        } else
            map.setMyLocationEnabled(true);
        if (TextUtils.isEmpty(selectCityName))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerModel.getPosition(), DEFAULT_ZOOM));
    }

    private void onError(Throwable throwable) {
        initExceptionHandler.showError(throwable, view -> loadList(new CitiesModel()));
    }
}
