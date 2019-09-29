package app.delivering.mvp.payment.list.regular.binder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.payment.regular.interactor.PutRegularPaymentInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.payment.list.adapter.PaymentsAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.payment.list.init.events.OnStartPaymentsFragmentEvent;
import app.delivering.mvp.payment.list.init.events.PaymentsInitBinderModelEvent;
import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;
import app.delivering.mvp.payment.list.init.exceptions.RxDialogCancelException;
import app.delivering.mvp.dialog.RxDialogHandler;
import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class PaymentsRegularBinder extends BaseBinder {
    @BindView(R.id.payments) RecyclerView paymentsRecyclerView;
    @BindView(R.id.payments_progress) View progress;
    @BindView(R.id.payments_container) View paymentsContainer;
    private final RxDialogHandler rxDialogHandler;
    private final PutRegularPaymentInteractor putRegularPaymentInteractor;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<String> replaySubject;
    private Subscription binderSubscribe;
    private PaymentsInitBinderModel event;

    public PaymentsRegularBinder(BaseActivity activity) {
        super(activity);
        replaySubject = ReplaySubject.create();
        rxDialogHandler = new RxDialogHandler(getActivity());
        putRegularPaymentInteractor = new PutRegularPaymentInteractor(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        setProgress(progress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentStart(PaymentsInitBinderModelEvent event) {
        this.event = event.getPaymentsInitBinderModel();
        initExceptionHandler.dismiss();

        binderSubscribe = rxDialogHandler.showTwoButtonsWithTitle(R.string.default_payment, R.string.default_payment_question, R.string.cancel, R.string.ok)
                .doOnNext(this::checkConfirmed)
                .doOnNext(isOk -> progressState())
                .concatMap(isOk -> putRegularPaymentInteractor.process(event.getPaymentsInitBinderModel().getCardId()))
                .subscribe(replaySubject);
    }

    private void checkConfirmed(Boolean isOk) {
        if (!isOk)
            throw new RxDialogCancelException();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentEvent(OnStartPaymentsFragmentEvent event) {
        if (hasToRestore(binderSubscribe))
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(String s) {
        resetState();
        ((PaymentsAdapter) paymentsRecyclerView.getAdapter()).updateRegular(event.getCardId());
    }

    private void showError(Throwable throwable) {
        resetState();
        if (!(throwable instanceof RxDialogCancelException))
            initExceptionHandler.showError(throwable, v -> EventBus.getDefault().post(event));
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
        paymentsRecyclerView.setEnabled(false);
        paymentsRecyclerView.setClickable(false);
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onAddPaymentEvent(new OnStartPaymentsFragmentEvent());
        paymentsRecyclerView.setEnabled(true);
        paymentsRecyclerView.setClickable(true);
    }

}
