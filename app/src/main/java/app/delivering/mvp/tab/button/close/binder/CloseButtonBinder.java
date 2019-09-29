package app.delivering.mvp.tab.button.close.binder;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkout.entity.CheckOutRequestBody;
import app.core.checkout.entity.PhoneVerificationException;
import app.delivering.component.bar.detail.receiver.ActivitiesBackStackReceiver;
import app.delivering.component.dialog.CustomUpdateDialog;
import app.delivering.component.payment.list.PaymentsActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.tab.button.close.events.OnCloseTabEvent;
import app.delivering.mvp.tab.button.close.presenter.TabClosePresenter;
import app.delivering.mvp.tab.button.uber.events.StartUberRequestEvent;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import app.delivering.mvp.tab.init.events.PauseTabEvent;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import app.qamode.log.LogToFileHandler;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class CloseButtonBinder extends BaseBinder {
    @BindView(R.id.tab_line_progress) MaterialProgressBar progressBar;
    @BindView(R.id.tab_close) Button closeTabButton;
    private TabClosePresenter tabClosePresenter;
    private ReplaySubject<CheckInResponse> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private RxDialogHandler rxDialogHandler;
    private final InitialTabActivityModel initialModel;
    private final CustomUpdateDialog customUpdateDialog;
    private boolean closedByUberCall;
    private CheckInResponse checkIn;


    public CloseButtonBinder(TabActivity activity) {
        super(activity);
        tabClosePresenter = new TabClosePresenter(getActivity());
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(activity);
        rxDialogHandler = new RxDialogHandler(activity);
        initialModel = activity.getInitialTabModel();
        customUpdateDialog = new CustomUpdateDialog(activity);
    }

    @OnClick(R.id.tab_close) public void close() {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - user taps CloseTab button");
        setUpUpdateDialog(true);
        EventBus.getDefault().postSticky(new OnCloseTabEvent(false));
    }

    @Override public void afterViewsBounded() {
        setUpUpdateDialog(false);
        setProgress(progressBar);
        initReplySubject();
        if (initialModel.isPhoneVerificationNeeded()) {
            startPhoneVerification();
        }
    }

    private void setUpUpdateDialog(boolean isStarting) {
        if (isStarting) {
            customUpdateDialog.show();
            customUpdateDialog.setMessage(getString(R.string.closing_tab));
        } else
            customUpdateDialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckInLoaded(CheckInResponse checkIn){
        this.checkIn = checkIn;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClose(OnCloseTabEvent event){
        LogToFileHandler.addLog("GetCheckOutGateway - button was pressed");
        showProgress();
        setUpUpdateDialog(true);
        closeTabButton.setEnabled(false);
        closedByUberCall = event.isClosedByUberCall();
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - OnCloseTabEvent received, id-" + initialModel.getCheckInId());
        tabClosePresenter.process(new CheckOutRequestBody(initialModel.getCheckInId(), closedByUberCall))
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPauseTabEvent(PauseTabEvent pauseTabEvent) {
        setUpUpdateDialog(false);
    }

    public void initReplySubject() {
        replaySubject = ReplaySubject.create();
        replaySubject.asObservable().observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError, ()->{
                    LogToFileHandler.addLog("GetCheckOutGateway - process interrupted");
                    hideProgress();
                    closeTabButton.setEnabled(true);
                });
    }

    private void show(CheckInResponse model) {
        LogToFileHandler.addLog("GetCheckOutGateway - result OK");
        hideProgress();
        closeTabButton.setEnabled(true);
        setUpUpdateDialog(false);
        sendBackStackReceiver();
        getActivity().finish();
        if (closedByUberCall)
            EventBus.getDefault().post(new StartUberRequestEvent());
        else
            showTabClosed(model.getCheckin());
    }

    private void sendBackStackReceiver() {
        Intent intent = new Intent(ActivitiesBackStackReceiver.REMOVE_BAR_DETAIL_ACTIVITY_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void showTabClosed(GetCheckInsResponse checkIn) {
        FillCloseTabActivityModel fillCloseTabActivityModel = new FillCloseTabActivityModel();
        fillCloseTabActivityModel.setCheckInId(checkIn.getId());
        fillCloseTabActivityModel.setDiscount(checkIn.getRideDiscount() != null ? checkIn.getRideDiscount().getDiscountValue() : 0.d);
        fillCloseTabActivityModel.setTabAutoClosed(checkIn.isAutoClosed());
        fillCloseTabActivityModel.setVendorId(checkIn.getVendorId());
        getActivity().startActivity(CloseTabActivity.startCloseTabActivity(getActivity(), fillCloseTabActivityModel));
    }

    private void onError(Throwable throwable) {
        if (throwable != null)
            LogToFileHandler.addLog("GetCheckOutGateway - error " + throwable.getClass().getSimpleName());
        hideProgress();
        closeTabButton.setEnabled(true);
        setUpUpdateDialog(false);
        checkErrorHandle(throwable);
        initReplySubject();
    }

    private void checkErrorHandle(Throwable throwable) {
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 402)
            showPaymentError();
        else if (throwable instanceof PhoneVerificationException)
            showPhoneVerificationDialog();
        else if (throwable instanceof HttpException) {
            if (((HttpException) throwable).code() == 409) {
                if (checkIn != null && checkIn.getCheckin().getBillItems().isEmpty())
                    showTabClosedEmptyError();
                else
                    showConflictError();
            } else if (((HttpException) throwable).code() == 500){
                showServerLostTicket();
            }
        } else
            initExceptionHandler.showError(throwable, v -> close());
    }

    private void showPaymentError() {
        rxDialogHandler
                .showTwoButtonsWithoutTitle(R.string.close_tab_payment_error, R.string.change_card, R.string.pay_with_cash)
                .subscribe(isOk -> {if (!isOk) showChangePaymentCard(); }, e -> {}, () -> {});
    }

    private void showChangePaymentCard() {
        getActivity().startActivity(new Intent(getActivity(), PaymentsActivity.class));
    }

    public void showPhoneVerificationDialog() {
        rxDialogHandler
                .showTwoButtonsWithoutTitle(R.string.please_verify_phone, R.string.cancel, R.string.verify_phone)
                .subscribe(isOk -> {
                    if (isOk)
                        startPhoneVerification();
                    else {
                        hideProgress();
                        initReplySubject();
                    } }, e->{}, ()->{});
    }

    private void startPhoneVerification() {
        getActivity().startActivityForResult(new Intent(getActivity(), VerifyPhoneNumberActivity.class),
                VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST);
    }

    private void showTabClosedEmptyError() {
        rxDialogHandler.showOneButtonWithTitle(R.string.whoops, R.string.tab_was_closed_empty,
                R.string.got_it).subscribe(isOk-> rxDialogHandler.dismissDialog(), e->{}, ()->{});
    }

    private void showConflictError() {
        rxDialogHandler
                .showTwoButtonsWithTitle(R.string.oops_conflict_title, R.string.tab_close_conflict_message, R.string.cancel, R.string.try_again)
                .subscribe(isOk -> { if (isOk) close(); }, e -> {}, () -> {});
    }

    private void showServerLostTicket() {
        rxDialogHandler.showOneButtonWithTitle(R.string.something_wrong, R.string.tab_close_poss_message, R.string.ok)
                .subscribe(isOk->{}, e->{}, ()->{});
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(CheckInResponse event){
        closeTabButton.setEnabled(true);
    }
}