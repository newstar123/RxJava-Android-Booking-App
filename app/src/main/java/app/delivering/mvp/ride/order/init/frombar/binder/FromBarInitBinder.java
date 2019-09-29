package app.delivering.mvp.ride.order.init.frombar.binder;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.core.bars.detail.interactor.GetItemBarInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.init.tobar.binder.subbinder.ViewPagerTypeChangeSubBinder;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class FromBarInitBinder extends BaseBinder {
    private final GetItemBarInteractor getItemBarInteractor;
    private ReplaySubject<BarModel> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private final ViewPagerTypeChangeSubBinder viewPagerTypeChangeSubBinder;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.order_ride_bar) TextView orderRideBarTextView;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.root_order_ride_bar) View rootOrderRideBarView;
    @BindViews({R.id.order_ride_address_divider_horizontal,
            R.id.order_ride_address,
            R.id.root_order_ride_bar}) List<View> orderViews;
    @BindView(R.id.order_ride_type_progress) MaterialProgressBar rideTypeProgressBar;

    public FromBarInitBinder(BaseActivity activity) {
        super(activity);
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(getActivity());
        viewPagerTypeChangeSubBinder = new ViewPagerTypeChangeSubBinder(getActivity());
        getItemBarInteractor = new GetItemBarInteractor(activity);
    }

    @Override public void afterViewsBounded() {
        if (getRideType(getActivity()) == InitialRideType.FROM_THE_VENUE) {
            progressState();
            long barId = getActivity()
                    .getIntent()
                    .getLongExtra(BarListItemClickBinder.DETAIL_BAR_ID, 0);
            getItemBarInteractor.process(barId).subscribe(replaySubject);
            rootOrderRideBarView.setVisibility(View.VISIBLE);
            moveRootOrderRideBarView();
            moveOrderRideBarEditText();
        }
    }

    private void moveRootOrderRideBarView() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootOrderRideBarView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ABOVE, 0);
        rootOrderRideBarView.setLayoutParams(layoutParams);
    }

    private void moveOrderRideBarEditText() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderRideAddressEditText.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.separator_view);
        orderRideAddressEditText.setLayoutParams(layoutParams);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OnOrderRideStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(BarModel barModel) {
        resetState();
        viewPagerTypeChangeSubBinder.apply();
        String name = barModel.getName();
        orderRideBarTextView.setText(name);
        LatLng barLatLng = new LatLng(barModel.getLatitude(), barModel.getLongitude());
        orderRideBarTextView.setTag(barLatLng);
        ButterKnife.apply(orderViews, ViewActionSetter.VISIBLE);
        rideTypeProgressBar.setVisibility(View.GONE);
        showBarPoint(barModel);
    }

    private void showBarPoint(BarModel barModel) {
        MapObservable.get(mapView).subscribe(map -> showMarkerAndZoom(barModel, map), err -> {});
    }

    private void showMarkerAndZoom(BarModel barModel, GoogleMap map) {
        Bitmap barPointBitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.order_ride_bar_point);
        BitmapDescriptor barPoint = BitmapDescriptorFactory.fromBitmap(barPointBitmap);
        LatLng latLng = new LatLng(barModel.getLatitude(), barModel.getLongitude());
        MarkerOptions barPointMarkerOption = new MarkerOptions().position(latLng).icon(barPoint)
                .anchor(0.5f, 0.5f);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        map.animateCamera(cu, 1000, null);
        map.addMarker(barPointMarkerOption);
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> afterViewsBounded());
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }

    public static InitialRideType getRideType(BaseActivity activity) {
        int value = activity.getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }
}
