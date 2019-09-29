package app.delivering.mvp.tab.init.binder;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Button;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.checkin.user.get.entity.RideSafeDiscountStatus;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.service.feedback.events.CheckOutEvent;
import app.delivering.mvp.tab.init.events.StartTabEvent;
import app.delivering.mvp.tab.init.events.UpdateTabEvent;
import app.delivering.mvp.tab.init.model.GetCheckInRequestModel;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import app.delivering.mvp.tab.init.presenter.TabInitPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class TabInitBinder extends BaseBinder {
    @BindView(R.id.tab_refresh_container) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tab_line_progress) MaterialProgressBar progressBar;
    @BindView(R.id.tab_bar_name) TextView vendorName;
    @BindView(R.id.tab_close) Button closeButton;
    private TabInitPresenter tabInitPresenter;
    private final InitExceptionHandler initExceptionHandler;
    private RxDialogHandler rxDialogHandler;
    private final InitialTabActivityModel initialModel;


    public TabInitBinder(TabActivity activity) {
        super(activity);
        tabInitPresenter = new TabInitPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(activity);
        rxDialogHandler = new RxDialogHandler(getActivity());
        initialModel = activity.getInitialTabModel();
    }

    @Override public void afterViewsBounded() {
        vendorName.setText(initialModel.getBarName());
        setProgress(progressBar);
        showProgress();
        swipeRefresh.setEnabled(true);
        swipeRefresh.setOnRefreshListener(() -> load(true));
        swipeRefresh.setColorSchemeResources(R.color.color_00a9e3, R.color.color_51d767);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getStartTabEvent(StartTabEvent event){
        load(false);
    }

    private void load(boolean isForced) {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - MyTab screen was shown");
        tabInitPresenter.process(new GetCheckInRequestModel(initialModel.getCheckInId(), isForced))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(checkInResponse -> show(checkInResponse), this::onError);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void update(UpdateTabEvent event){
        show(event.getCheckIn());
    }

    private void show(CheckInResponse checkInResponse) {
        if (checkInResponse.getCheckin().getCheckoutTime() == null)
            updateTabInformation(checkInResponse);
        else {
            CheckOutEvent checkOutEvent = new CheckOutEvent();
            checkOutEvent.setCheckInResponse(checkInResponse);
            EventBus.getDefault().postSticky(checkOutEvent);
        }
    }

    private void updateTabInformation(CheckInResponse checkInResponse) {
        restoreState();
        EventBus.getDefault().post(checkInResponse);
        showUpdatedRideDiscountStatus(checkInResponse);
    }

    private void showUpdatedRideDiscountStatus(CheckInResponse checkInResponse) {
        if (checkInResponse.isFreeRideAlreadyAvailable()) {
            RideSafeDiscountStatus rideSafeDiscountStatus = checkInResponse.getCheckin().getRideSafeDiscountStatus();
            rxDialogHandler.showOneButtonWithTitle(getString(R.string.headsup_with_exclamation),
                    String.format(getString(R.string.free_ride_already_added),
                            (rideSafeDiscountStatus.getMinSpendToRideDiscount() == 0) ? 60 : String.valueOf(rideSafeDiscountStatus.getMinSpendToRideDiscount() / 60)),
                    R.string.ok)
                    .subscribe(isOk -> rxDialogHandler.dismissDialog(), e -> { }, () -> { });
        } else
            if ((checkInResponse.getCheckin().getRideDiscount() == null ||
                    checkInResponse.getCheckin().getRideDiscount().getDiscountValue() == 0d || !checkInResponse.isDiscountAvailable()) &&
                    checkInResponse.isMarkAlreadyDisplayed())
                rxDialogHandler.showOneButtonWithoutTitle(R.string.free_ride_is_unavailable, R.string.ok)
                    .subscribe(isOk -> rxDialogHandler.dismissDialog(), e -> {}, () -> {});
    }

    private void onError(Throwable throwable) {
        restoreState();
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 409)
            rxDialogHandler.showTwoButtonsWithTitle(R.string.error, R.string.tab_update_conflict_message, R.string.cancel, R.string.checkout_close_tab)
                    .subscribe(isOk -> {if(isOk)closeButton.performClick();});
        else
            initExceptionHandler.showError(throwable, v -> load(false));
    }

    private void restoreState() {
        swipeRefresh.setRefreshing(false);
        hideProgress();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCheckOut(CheckOutEvent event){
        getActivity().finish();
    }
}
