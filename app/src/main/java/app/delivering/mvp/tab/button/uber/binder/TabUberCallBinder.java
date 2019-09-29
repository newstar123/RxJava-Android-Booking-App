package app.delivering.mvp.tab.button.uber.binder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkout.entity.PhoneVerificationException;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.tab.button.close.events.OnCloseTabEvent;
import app.delivering.mvp.tab.button.uber.events.StartUberRequestEvent;
import app.delivering.mvp.tab.button.uber.presenter.TabUberCallPresenter;
import app.qamode.log.LogToFileHandler;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class TabUberCallBinder extends BaseBinder {
    @BindView(R.id.tab_line_progress) MaterialProgressBar progressBar;
    @BindView(R.id.tab_uber_button) View callUberButton;
    private RxDialogHandler dialogHandler;
    private final TabUberCallPresenter tabUberCallPresenter;
    private long vendorId;
    private double discount;

    public TabUberCallBinder(BaseActivity activity) {
        super(activity);
        dialogHandler = new RxDialogHandler(getActivity());
        tabUberCallPresenter = new TabUberCallPresenter(activity);
    }

    @Override
    public void afterViewsBounded() {
        setProgress(progressBar);
        unlockButton(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckInInformationReady(CheckInResponse checkInResponse){
        vendorId = checkInResponse.getCheckin().getVendorId();
        if (checkInResponse.getCheckin().getRideDiscount() != null)
            discount = checkInResponse.getCheckin().getRideDiscount().getDiscountValue();
        unlockButton(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startUberRequest(StartUberRequestEvent uberEvent){
        Intent intent = new Intent(getActivity(), OrderRideActivity.class);
        intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, vendorId);
        intent.putExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, discount);
        intent.putExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, InitialRideType.FROM_THE_VENUE.getValue());
        getActivity().startActivityForResult(intent, OrderRideActivity.ORDER_RIDE_REQUEST);
    }

    @OnClick(R.id.tab_uber_button) public void uberCall() {
        showProgress();
        unlockButton(false);
        tabUberCallPresenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> startOrderRideActivity(), this::error, () -> unlockButton(true));
    }

    private void startOrderRideActivity() {
        hideProgress();
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
            LogToFileHandler.addLog("GetCheckInByIdRestGateway - user taps startOrderRideActivity");
            EventBus.getDefault().postSticky(new OnCloseTabEvent(true));
        } catch (PackageManager.NameNotFoundException e) {
            showDialog();
        }
    }

    private void error(Throwable throwable) {
        hideProgress();
        if (throwable instanceof PhoneVerificationException) {
            dialogHandler
                    .showTwoButtonsWithoutTitle(R.string.please_verify_phone, R.string.cancel, R.string.verify_phone)
                    .subscribe(isOk -> {
                        if (isOk)
                            startPhoneVerification();
                    }, this::dialogError, () -> dialogHandler.dismissDialog());
        }
        unlockButton(true);
    }

    private void dialogError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void startPhoneVerification() {
        getActivity().startActivityForResult(new Intent(getActivity(), VerifyPhoneNumberActivity.class),
                VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST_FOR_UBER);
    }

    private void showDialog() {
        dialogHandler.showTwoButtonsWithTitle(R.string.uber_required_title, R.string.uber_required_message, R.string.cancel, R.string.ok)
                .filter(isOk -> isOk)
                .doOnNext(aBoolean -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.uber_app_url)));
                    getActivity().startActivityForResult(intent, TabActivity.INSTALL_UBER_KEY);
                })
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(r -> {}, Throwable::printStackTrace);
    }

    private void unlockButton(boolean isEnabled) {
        callUberButton.setEnabled(isEnabled);
    }
}
