package app.delivering.mvp.ride.order.address.change.binder;


import android.support.v4.view.ViewPager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;
import app.delivering.mvp.ride.order.address.change.events.ChangeAddressEvent;
import app.delivering.mvp.ride.order.address.change.presenter.ChangeAddressPresenter;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.route.apply.custom.events.ApplyPickUpAddressEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ChangeAddressBinder extends BaseBinder {
    private final ChangeAddressPresenter changeAddressPresenter;
    private ReplaySubject<Place> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    @BindView(R.id.order_ride_pickup_estimate) TextView orderRidePickupEstimateTextView;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindView(R.id.order_ride_type_pager) ViewPager orderRideTypeViewPager;
    private ChangeAddressEvent event;

    public ChangeAddressBinder(BaseActivity activity) {
        super(activity);
        changeAddressPresenter = new ChangeAddressPresenter(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeAddressEvent(ChangeAddressEvent event) {
        this.event = event;
        showProgress();
        changeAddressPresenter.process(event.getModel().getPlaceId())
                .subscribe(replaySubject);
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

    private void show(Place place) {
        resetState();
        orderRidePickupEstimateTextView.setTag(null);
        orderRideTypeViewPager.setTag(null);
        if (orderRideAddressEditText.isFocused())
            EventBus.getDefault().post(new ApplyAddressEvent(place));
        if (orderPickUpAddress.isFocused())
            EventBus.getDefault().post(new ApplyPickUpAddressEvent(place));
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
        resetState();
        initExceptionHandler.showError(throwable, v -> onChangeAddressEvent(event));
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
}
