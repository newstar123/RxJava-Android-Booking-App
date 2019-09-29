package app.delivering.mvp.tab.discount.binder;

import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import app.R;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.tab.discount.model.DiscountModel;
import app.delivering.mvp.tab.discount.presenter.TabDiscountInitPresenter;
import app.delivering.mvp.tab.init.events.StartTabEvent;
import app.delivering.mvp.tab.init.events.StopTabEvent;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TabDiscountInitBinder extends BaseBinder {
    @BindView(R.id.tab_line_progress) MaterialProgressBar progressBar;
    @BindView(R.id.tab_free_uber_timer) TextView uberTimer;
    @BindView(R.id.tab_free_uber_title) TextView uberTitle;

    @BindView(R.id.tab_uber_max_discount) TextView uberDiscount;
    @BindView(R.id.word_free) TextView freeUberWord;

    private TabDiscountInitPresenter discountInitPresenter;
    private Subscription timerSubscription;
    private final InitExceptionHandler initExceptionHandler;

    private static final String EMPTY_STRING_VALUE = "";
    private double discount;

    public TabDiscountInitBinder(BaseActivity activity) {
        super(activity);
        discountInitPresenter = new TabDiscountInitPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        showProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(CheckInResponse checkInResponse) {
        if (checkInResponse.getCheckin().getRideSafeDiscountStatus() == null)
            setUpUberViewsVisibility(false);
        else {
            discountInitPresenter.process(checkInResponse.getCheckin())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(this::show, this::onError);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getStopTabEvent(StopTabEvent stopTabEvent) {
        uberTimer.setText(EMPTY_STRING_VALUE);
    }

    private void show(DiscountModel discountModel) {
        hideProgress();
        discount = discountModel.getMaxDiscount();

        if (discountModel.isFreeRideAndNoDiscount()) {
            setUpUberViewsVisibility(true);
            setUpUberTimer(discountModel);
            uberTitle.setText(formatHtmlToSpanned(R.string.remaining_for_uber));
        }
        if (discountModel.isNoFreeRideAndNoDiscount()) {
            stopExistingUberTimer();
            setUpUberViewsVisibility(false);
            setUpDiscount(false);
        }
        if (discountModel.isFreeRideAndDiscount()) {
            stopExistingUberTimer();
            setUpUberViewsVisibility(false);
            setUpDiscount(true);
        }
        if (discountModel.isDiscountAndNoFreeRide()) {
            stopExistingUberTimer();
            setUpUberViewsVisibility(false);
            setUpDiscount(true);
        }
    }

    private void setUpDiscount(boolean isVisible) {
        uberDiscount.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        freeUberWord.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (isVisible)
            uberDiscount.setText(String.format(getString(R.string.dollars_value_max), String.valueOf(discount)));
    }

    private void stopExistingUberTimer() {
        if (timerSubscription != null && !timerSubscription.isUnsubscribed())
            timerSubscription.unsubscribe();
    }

    private void setUpUberTimer(final DiscountModel discountModel) {
        int discountTime = (int) discountModel.getTime();
        stopExistingUberTimer();

        if (discountTime <= 0)
            setUpDiscount(true);
        else {
            timerSubscription = Observable.zip(Observable.range(0, discountTime), Observable.interval(1, TimeUnit.SECONDS),
                    (rangeValue, interValue) -> discountTime - rangeValue)
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .observeOn(Schedulers.io())
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onCompleted() {
                            getActivity().runOnUiThread(() -> setUpUberViewsVisibility(false));
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Integer integer) {
                            getActivity().runOnUiThread(() -> {
                                if (uberTimer != null) {
                                    convertTime(integer);
                                    if (integer <= 1)
                                        setUpDiscount(true);
                                }
                            });
                        }
                    });
        }
    }

    private void convertTime(int timeSec) {
        int min = (timeSec % 3600) / 60;
        int sec = timeSec % 60;
        uberTimer.setText(String.format(getString(R.string.tab_time_format), min, sec));
    }

    private void setUpUberViewsVisibility(boolean isVisible) {
        if (uberTimer != null && uberTitle != null) {
            uberTimer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            uberTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void onError(Throwable throwable) {
        hideProgress();
        initExceptionHandler.showError(throwable, v -> EventBus.getDefault().post(new StartTabEvent()));
    }

    @OnClick(R.id.tab_back_button) void onBack() {
        getActivity().onBackPressed();
    }
}
