package app.delivering.mvp.bars.detail.checkin.open.binder;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.beacon.bluetooth.entity.BluetoothStateException;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkin.user.post.entity.CheckPhotoException;
import app.core.checkin.user.post.entity.EmailVerificationException;
import app.core.checkin.user.post.interactor.exception.CheckInAlreadyInUseException;
import app.core.init.token.entity.NoAccountException;
import app.core.login.check.entity.GuestUserException;
import app.core.payment.get.entity.NoPaymentException;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.bar.detail.checkin.CheckInSplashScreenActivity;
import app.delivering.component.dialog.CustomUpdateDialog;
import app.delivering.component.main.MainActivity;
import app.delivering.component.payment.add.AddPaymentActivity;
import app.delivering.component.payment.list.PaymentsActivity;
import app.delivering.component.verify.VerifyEmailActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabEvent;
import app.delivering.mvp.bars.detail.checkin.open.events.UpdateAfterAddingPhotoEvent;
import app.delivering.mvp.bars.detail.checkin.open.events.UpdateOpenTabViewEvent;
import app.delivering.mvp.bars.detail.checkin.open.events.UpdatePhotoEvent;
import app.delivering.mvp.bars.detail.checkin.open.presenter.BarDetailCheckinPresenter;
import app.delivering.mvp.bars.detail.checkin.open.presenter.CacheInterface;
import app.delivering.mvp.bars.detail.checkin.signup.events.SignUpFromBarDetailEvent;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionViewTabEvent;
import app.delivering.mvp.coach.login.init.events.HideFBLoginCoachMarkEvent;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.init.model.ResponseErrModel;
import app.delivering.mvp.main.photo.autoopen.events.ProfileUpdatedEvent;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.payment.list.init.events.OnDestroyPaymentsListEvent;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailCheckinOpenTabBinder extends BaseBinder {
    private static final int PRE_AUTH_FUNDS_ERROR = 402;
    private static final int TICKET_NAME_ERROR = 409;
    private BarDetailCheckinPresenter presenter;
    private final CacheInterface cacheInterface;
    private final InitExceptionHandler initExceptionHandler;
    private RxDialogHandler rxDialogHandler;
    private OpenTabEvent openTabEvent;
    private CustomUpdateDialog updateDialog;

    public BarDetailCheckinOpenTabBinder(BarDetailActivity activity) {
        super(activity);
        presenter = new BarDetailCheckinPresenter(getActivity());
        cacheInterface = presenter;
        initExceptionHandler = new InitExceptionHandler(getActivity());
        rxDialogHandler = new RxDialogHandler(getActivity());
        updateDialog = new CustomUpdateDialog(getActivity());
        if (activity.getIntent().getBooleanExtra(QorumNotifier.CHANGE_PAYMENT_METHOD, false) ||
                ((BarDetailActivity)getActivity()).getInitialModel().isShouldChangePaymentMethod()) {
            prepareOpenTabEvent();
            showAddPayment();
        }
        if (activity.getIntent().getBooleanExtra(QorumNotifier.EMAIL_VERIFICATION_ERROR, false) ||
                ((BarDetailActivity)getActivity()).getInitialModel().isShouldVerifyEmail()) {
            prepareOpenTabEvent();
            startEmailVerification();
        }
    }

    private void prepareOpenTabEvent() {
        openTabEvent = new OpenTabEvent();
        openTabEvent.setCurrentBarId(getVenueId());
        openTabEvent.setIgnoreAnotherCheckIns(true);
        openTabEvent.setIgnoreBluetoothState(true);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onOpen(OpenTabEvent openTabEvent) {
        EventBus.getDefault().removeStickyEvent(OpenTabEvent.class);
        this.openTabEvent = openTabEvent;
        updateDialog.show();
        updateDialog.setMessage(getString(R.string.checking_preconditions));
        openTabEvent.setCurrentBarId(getVenueId());
        presenter.process(openTabEvent)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(this::show, this::onError, () -> {
                    if (updateDialog != null && updateDialog.isShowing()) updateDialog.dismiss();
                });
    }

    private long getVenueId() {
        return ((BarDetailActivity)getActivity()).getInitialModel().getBarId();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateAfterChangePhoto(UpdateAfterAddingPhotoEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (openTabEvent != null)
            onOpen(openTabEvent);
    }

    @Subscribe()
    public void onUpdateProgressDialog(UpdateOpenTabViewEvent event) {
        getActivity().runOnUiThread(() -> updateDialog.setMessage(getString(R.string.opening_tab)));
    }

    private void show(CheckInResponse checkIn) {
        updateDialog.dismiss();
        Intent intent = new Intent(getActivity(), CheckInSplashScreenActivity.class);
        Bundle bundle = new Bundle();
        InitialTabActivityModel model = new InitialTabActivityModel();
        model.setCheckInId(checkIn.getCheckin().getId());
        model.setBarId(checkIn.getCheckin().getVendor().getId());
        model.setBarName(checkIn.getCheckin().getVendor().getName());
        bundle.putParcelable(CheckInSplashScreenActivity.CHECK_IN_RESULT_KEY, model);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    private void onError(Throwable throwable) {
        updateDialog.dismiss();
        Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0))
                .subscribe(s -> {}, err -> {}, () -> {});
        cacheInterface.saveToCache(0);
        if (throwable instanceof NoPaymentException)
            showAddPaymentDialog();
        else if (throwable instanceof NoAccountException || throwable instanceof GuestUserException)
            showLoginDialog();
        else if (throwable instanceof BluetoothStateException)
            showBluetoothStateDialog();
        else if (throwable instanceof EmailVerificationException)
            showEmailVerificationDialog();
        else if (throwable instanceof CheckPhotoException)
            showAddPhotoDialog();
        else if (throwable instanceof HttpException)
            showTicketNameErrorDialog((HttpException) throwable);
        else if (throwable instanceof CheckInAlreadyInUseException)
            showTabAlreadyOpenedDialog();
        else
            initExceptionHandler.showErrorWithDuration(throwable, view -> onOpen(new OpenTabEvent()));
    }

    private void showTabAlreadyOpenedDialog() {
        rxDialogHandler.showOneButtonWithoutTitle(R.string.pos_error_409_checkin_error, R.string.ok)
                .subscribe(val -> {
                    rxDialogHandler.dismissDialog();
                    EventBus.getDefault().postSticky(new ActionViewTabEvent());
                }, err -> {}, () -> {});
    }

    private void showAddPaymentDialog() {
        rxDialogHandler.showTwoButtonsWithTitle(R.string.open_tab_dialog_title, R.string.need_payment_message, R.string.cancel, R.string.add_card)
                .subscribe(isOk -> {
                    if (isOk)
                        showAddPayment();}, e -> {}, () -> {});
    }

    private void showAddPayment() {
        Intent intent = new Intent(getActivity(), AddPaymentActivity.class);
        intent.putExtra(AddPaymentActivity.CHECK_IN_WITHOUT_PAYMENT_KEY, true);
        getActivity().startActivityForResult(intent, AddPaymentActivity.PAYMENT_ADDED_CODE);
    }

    private void showAddPhotoDialog() {
        Observable.just(true)
                .doOnNext(val -> QorumSharedCache.checkProfilePhotoVal().save(BaseCacheType.BOOLEAN, val))
                .subscribe(ok -> {}, err -> {}, () -> {});

        rxDialogHandler.showTwoButtonsWithoutTitle(R.string.add_profile_picture_TAB_updating, R.string.cancel, R.string.add_picture)
                .subscribe(clickAddPhoto -> { if (clickAddPhoto) sendEventForAddingPhoto(); });
    }

    private void sendEventForAddingPhoto() {
        EventBus.getDefault().postSticky(new UpdatePhotoEvent());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
    }

    private void showLoginDialog() {
        rxDialogHandler.showOneButtonWithTitle(R.string.phrase_log_in, R.string.place_log_in, R.string.log_in)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::openFBLoginPage, e -> {}, () -> {});
    }

    private void openFBLoginPage(Boolean isOk) {
        if (isOk) {
            EventBus.getDefault().post(new HideFBLoginCoachMarkEvent());
            EventBus.getDefault().post(new SignUpFromBarDetailEvent(true));
        }
    }

    private void showBluetoothStateDialog() {
        rxDialogHandler.showTwoButtonsWithTitle(R.string.open_tab_dialog_title, R.string.error_bluetooth_not_enabled, R.string.cancel, R.string.enable)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::restartIgnoreBluetoothState,
                           e -> restartIgnoreBluetoothState(false), () -> {});
    }

    private void restartIgnoreBluetoothState(boolean isOk) {
        if (isOk)
            activateBluetooth();
        openTabEvent.setIgnoreBluetoothState(true);
        onOpen(openTabEvent);
    }

    private void activateBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null)
            bluetoothAdapter.enable();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void restartIgnoreProfilePhotoState(ProfileUpdatedEvent event) {
        onOpen(openTabEvent);
    }

    private void showPreAuthFundsErrorDialog(String title) {
        rxDialogHandler.showTwoButtonsWithTitle(title, R.string.please_change_payment_card, R.string.cancel, R.string.change_payment)
                .subscribe(isOk -> {
                    if (isOk)
                        getActivity().startActivity(new Intent(getActivity(), PaymentsActivity.class));
                }, e -> {}, () -> {});
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void restartAfterChangePayment(OnDestroyPaymentsListEvent event) {
        onOpen(openTabEvent);
    }

    private void showTicketNameErrorDialog(HttpException exception) {
        ResponseErrModel responseErrModel = initExceptionHandler.parseResponseError(exception);

        int status;
        try {
            status = Integer.parseInt(responseErrModel.getStatus());
        } catch (Exception parsingException) {
            status = -1;
        }

        final String metaMessage = responseErrModel.getMetaMessage();
        final String title = responseErrModel.getTitle();

        switch (status) {
            case PRE_AUTH_FUNDS_ERROR: {
                showPreAuthFundsErrorDialog(title);
                break;
            }
            case TICKET_NAME_ERROR: {
                if (metaMessage.equals(getString(R.string.pos_threw_error)))
                    showResponseErrDialog(title, getString(R.string.pos_error_409_server_error), R.string.cancel, R.string.try_again);
                else if (metaMessage.equals(getString(R.string.request_failed_conflict)) ||
                        metaMessage.equals(getString(R.string.request_failed_conflict_dup)))
                    showResponseErrDialog(title, getString(R.string.pos_error_409_checkin_error), R.string.cancel, R.string.try_again);
                else
                    showResponseErrDialog(title, exception.response().message(), R.string.cancel, R.string.try_again);
                break;
            }
            default:
                rxDialogHandler.showOneButtonWithTitle(R.string.pos_error_500_n_title, R.string.could_not_check_in, R.string.cancel)
                        .subscribe(isOk -> { if (isOk) rxDialogHandler.dismissDialog(); }, e -> {}, () -> {});
        }
    }

    private void showResponseErrDialog(String title, String message, @StringRes int button1, @StringRes int button2) {
        rxDialogHandler.showTwoButtonsWithTitle(title, message, button1, button2)
                .subscribe(isOk -> { if (isOk) onOpen(new OpenTabEvent()); }, e -> {}, () -> {});
    }

    private void showEmailVerificationDialog() {
        rxDialogHandler.showTwoButtonsWithoutTitle(R.string.please_verify_email, R.string.cancel, R.string.verify_email)
                .subscribe(isOk -> {
                    if (isOk) startEmailVerification();
                }, e -> {}, () -> {});
    }

    private void startEmailVerification() {
        getActivity().startActivity(new Intent(getActivity(), VerifyEmailActivity.class));
    }
}
