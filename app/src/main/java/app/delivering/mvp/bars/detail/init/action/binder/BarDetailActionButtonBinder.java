package app.delivering.mvp.bars.detail.init.action.binder;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.permission.entity.GetAccountPermissionException;
import app.core.permission.entity.LocationPermissionException;
import app.core.permission.entity.NetworkSettingsException;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.bar.detail.action.BarDetailActionButtonState;
import app.delivering.component.bar.detail.action.CustomBottomActionButton;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionOpenTabEvent;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionViewTabEvent;
import app.delivering.mvp.bars.detail.init.action.presenter.BarDetailCheckinInitButtonPresenter;
import app.delivering.mvp.bars.detail.init.get.events.BarDetailStartEvent;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.bars.detail.init.ubercall.init.model.GetUberEstimationsModel;
import app.delivering.mvp.bars.detail.init.ubercall.init.presenter.GetUberEstimationPresenter;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.work.BarWorkTypeParser;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import butterknife.BindView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailActionButtonBinder extends BaseBinder {
    private static final double MINIMAL_DISTANCE_IN_KM = 0.25;

    @BindView(R.id.bar_detail_action_button) CustomBottomActionButton actionButton;
    private final GetUberEstimationPresenter uberEstimationPresenter;
    private final BarDetailCheckinInitButtonPresenter checkinInitButtonPresenter;
    private final InitExceptionHandler initExceptionHandler;
    private BarDetailActivity activity;
    private double distance;
    private BarDetailModel detailModel;
    private boolean isOpenedBySwipe;
    private boolean shouldOpenTabByNotification;
    private boolean shouldViewTabByNotification;
    private InitialVenueDetailModel initialModel;
    private String workTypeName;

    public BarDetailActionButtonBinder(BarDetailActivity activity) {
        super(activity);
        this.activity = activity;
        initExceptionHandler = new InitExceptionHandler(getActivity());
        uberEstimationPresenter = new GetUberEstimationPresenter(getActivity());
        checkinInitButtonPresenter = new BarDetailCheckinInitButtonPresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        actionButton.setActionState(BarDetailActionButtonState.UBER_CALL);
        getInitialParams();
        if (!TextUtils.isEmpty(workTypeName) && BarByWorkTime.valueOf(workTypeName) == BarByWorkTime.CLOSED)
            actionButton.setActionState(BarDetailActionButtonState.CLOSED);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void open(BarDetailStartEvent startEvent) {
        if (detailModel != null) {
            getInitialParams();
            showDetail(detailModel);
        }
    }

    private void getInitialParams() {
        initialModel = ((BarDetailActivity) getActivity()).getInitialModel();
        distance = initialModel.getDistanceKm();
        isOpenedBySwipe = initialModel.isShouldOpenBySwiping();
        shouldOpenTabByNotification = activity.getIntent().getBooleanExtra(QorumNotifier.NOTIFICATION_OPEN_TAB, false);
        if (!shouldOpenTabByNotification)
            shouldOpenTabByNotification = initialModel.isShouldAutoOpenTab();
        shouldViewTabByNotification = activity.getIntent().getBooleanExtra(TabStatusForegroundService.VIEW_CHECK_IN, false);
        workTypeName = initialModel.getBarWorkType();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        actionButton.setEnabled(false);
        this.detailModel = detailModel;
        checkinInitButtonPresenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::tryShowTab);
    }

    private void tryShowTab(Long savedBarId) {
        if (savedBarId != 0 && savedBarId == initialModel.getBarId()) {
            actionButton.setEnabled(true);
            actionButton.setActionState(BarDetailActionButtonState.VIEW_TAB);
        } else if (distance < MINIMAL_DISTANCE_IN_KM)
            checkWorkType();

        if (actionButton.barDetailActionState() == BarDetailActionButtonState.UBER_CALL)
            estimateUber(detailModel);

        if (actionButton.barDetailActionState() == BarDetailActionButtonState.OPEN_TAB ||
                actionButton.barDetailActionState() == BarDetailActionButtonState.VIEW_TAB){
                checkIsNeedsAutoRedirectToMyTabScreen();
        }
    }

    private void checkIsNeedsAutoRedirectToMyTabScreen() {
        if (shouldOpenTabByNotification)
            tryOpenTab();
        if (shouldViewTabByNotification)
            tryViewTab();
        if (isOpenedBySwipe && initialModel.getSwipingText() != null) {
            if (initialModel.getSwipingText().equals(getString(R.string.open_tab)))
                tryOpenTab();
            if (initialModel.getSwipingText().equals(getString(R.string.view_tab)))
                tryViewTab();
        }
        initialModel.setShouldOpenBySwiping(false);
        initialModel.setShouldAutoOpenTab(false);
        initialModel.setSwipingText("");
        getActivity().setIntent(BarDetailActivity.updateIntent((getActivity()).getIntent(), initialModel));
    }

    private void tryOpenTab() {
        EventBus.getDefault().postSticky(new ActionOpenTabEvent());
    }

    private void tryViewTab() {
        EventBus.getDefault().postSticky(new ActionViewTabEvent());
    }

    private void checkWorkType() {
        Observable.just(BarWorkTypeParser.createWorkTypeInformation(detailModel.getBarModel()))
                .subscribe(o -> checkClose(o.getBarWorkTimeType()), e -> actionButton.setEnabled(true), () -> { });
    }

    private void estimateUber(BarDetailModel detailModel) {
        actionButton.setActionState(BarDetailActionButtonState.UBER_CALL);
        double latitude = detailModel.getBarModel().getLatitude();
        double longitude = detailModel.getBarModel().getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        uberEstimationPresenter.process(latLng)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::showButton, this::onError);
    }

    private void showButton(GetUberEstimationsModel getUberEstimationsModel) {
        actionButton.setEnabled(true);
        actionButton.setUberEstimation(getUberEstimationsModel.getUberTripEstimation());
    }

    private void onError(Throwable throwable) {
        actionButton.setEnabled(true);
        if (throwable instanceof GetAccountPermissionException ||
                throwable instanceof NetworkSettingsException ||
                throwable instanceof LocationPermissionException)
            initExceptionHandler.showError(throwable, view -> estimateUber(detailModel));
        else if (throwable instanceof HttpException)
            checkErrorByCode((HttpException) throwable);
        else if (throwable instanceof IndexOutOfBoundsException)
            showEstimateError(R.string.too_far);
    }

    private void checkErrorByCode(HttpException error) {
        if (error.code() == 401)
            showEstimateError(R.string.guest_mode);
        if (error.code() == 500)
            showEstimateError(R.string.too_far);
    }

    private void showEstimateError(int errorTextId) {
        actionButton.setUberEstimation(getString(errorTextId));
    }

    private void checkClose(BarByWorkTime time) {
        if (time == BarByWorkTime.CLOSED)
            actionButton.setActionState(BarDetailActionButtonState.CLOSED);
        else {
            actionButton.setActionState(BarDetailActionButtonState.OPEN_TAB);
            actionButton.setEnabled(true);
        }
    }
}