package app.delivering.mvp.ride.order.type.apply.binder;


import android.support.v4.view.ViewPager;
import android.view.View;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.pager.RideTypeAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.fare.apply.events.ApplyFareEvent;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.type.apply.binder.subbinder.ViewPagerIndicatorSubBinder;
import app.delivering.mvp.ride.order.type.apply.events.ApplyTypesEvent;
import app.delivering.mvp.ride.order.type.apply.model.ApplyTypesModel;
import app.delivering.mvp.ride.order.type.apply.presenter.ApplyTypePresenter;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ApplyTypesBinder extends BaseBinder {
    private final ViewPagerIndicatorSubBinder viewPagerIndicatorSubBinder;
    @BindView(R.id.order_ride_type_progress) MaterialProgressBar rideTypeProgressBar;
    @BindView(R.id.order_ride_type_pager) ViewPager orderRideTypeViewPager;
    @BindView(R.id.order_ride_commit_ride_type) View commitTypeButton;
    @BindViews({R.id.order_ride_type_pager_indicator,
            R.id.order_ride_type_pager}) List<View> typePagerViews;
    private final ApplyTypePresenter applyTypePresenter;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<ApplyTypesModel> replaySubject;
    private ApplyTypesEvent event;

    public ApplyTypesBinder(BaseActivity activity) {
        super(activity);
        applyTypePresenter = new ApplyTypePresenter(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        viewPagerIndicatorSubBinder = new ViewPagerIndicatorSubBinder(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyTypes(ApplyTypesEvent event) {
        this.event = event;
        showProgress();
        Object tag = orderRideTypeViewPager.getTag();
        if (tag == null)
            getTypes();
        else
            show((ApplyTypesModel) tag);
    }

    private void getTypes() {
        applyTypePresenter.process(event.getRoute())
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

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
        resetState();
        initExceptionHandler.showError(throwable, v -> onApplyTypes(event));
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void show(ApplyTypesModel model) {
        resetState();
        if (isInChooseTypeMode())
            applyTypesAndContinue(model);
    }

    private boolean isInChooseTypeMode() {
        return findViewById(R.id.order_ride_information_section).getLayoutParams().height
                == (int) getActivity().getResources().getDimension(R.dimen.dip270);
    }

    private void applyTypesAndContinue(ApplyTypesModel model) {
        orderRideTypeViewPager.setTag(model);
        OrderRideAnimation.run(getActivity());
        ButterKnife.apply(typePagerViews, ViewActionSetter.VISIBLE);
        commitTypeButton.setVisibility(View.VISIBLE);
        rideTypeProgressBar.setVisibility(View.GONE);
        viewPagerIndicatorSubBinder.apply(model.getRideWithCategories());
        RideTypeAdapter rideTypeAdapter = new RideTypeAdapter(getActivity());
        orderRideTypeViewPager.setAdapter(rideTypeAdapter);
        rideTypeAdapter.setData(model.getRideWithCategories());
        sendApplyFareEvent(model);
    }

    private void sendApplyFareEvent(ApplyTypesModel model) {
        ApplyFareEvent event = new ApplyFareEvent();
        event.setProducts(model.getProducts());
        event.setRoute(model.getRoute());
        EventBus.getDefault().post(event);
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }
}
