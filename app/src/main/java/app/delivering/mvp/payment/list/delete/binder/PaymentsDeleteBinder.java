package app.delivering.mvp.payment.list.delete.binder;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.payment.list.adapter.PaymentsAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.payment.list.delete.model.PaymentsDeleteModel;
import app.delivering.mvp.payment.list.delete.model.PaymentsErrorDeleteModel;
import app.delivering.mvp.payment.list.delete.presenter.PaymentsDeletePresenter;
import app.delivering.mvp.payment.list.init.events.OnStartPaymentsFragmentEvent;
import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;


public class PaymentsDeleteBinder extends BaseBinder {
    private final RxSnackBar rxSnackBar;
    private final InitExceptionHandler initExceptionHandler;
    private final PaymentsDeletePresenter paymentsDeletePresenter;
    @BindView(R.id.payments) RecyclerView paymentsRecyclerView;
    @BindView(R.id.payments_container) CoordinatorLayout coordinatorLayout;
    private ReplaySubject<PaymentsErrorDeleteModel> replaySubject;
    private Subscription binderSubscribe;
    private PaymentsAdapter adapter;

    public PaymentsDeleteBinder(BaseActivity activity) {
        super(activity);
        rxSnackBar = new RxSnackBar(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        paymentsDeletePresenter = new PaymentsDeletePresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        adapter = (PaymentsAdapter) paymentsRecyclerView.getAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaymentsDeleteModel(PaymentsDeleteModel event) {
        resetState();
        binderSubscribe = rxSnackBar.show(coordinatorLayout, R.string.payment_card_deleted)
                .doOnNext(isUndo -> restoreIfUndo(isUndo, event))
                .filter(isUndo -> !isUndo)
                .concatMap(isUndo -> paymentsDeletePresenter.process(event))
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentEvent(OnStartPaymentsFragmentEvent event) {
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, e -> {});
    }

    private void show(PaymentsErrorDeleteModel response) {
        if (response.hasError())
            showError(response);
        else
            adapter.updateRegular(response.getDefaultCard());
        resetState();
    }

    private void showError(PaymentsErrorDeleteModel response) {
        PaymentsDeleteModel event = response.getEvent();
        Throwable throwable = response.getThrowable();
        restoreIfUndo(true, event);
        initExceptionHandler.showError(throwable, view -> retry(event));
    }

    private void retry(PaymentsDeleteModel event) {
        adapter.removeItem(event.getPaymentsInitBinderModel(), event.getPosition());
    }

    private void restoreIfUndo(Boolean isUndo, PaymentsDeleteModel event) {
        if (isUndo)
            adapter.setModel(event.getPaymentsInitBinderModel(), event.getPosition());

    }

    private void resetState() {
        replaySubject = ReplaySubject.create();
        onAddPaymentEvent(new OnStartPaymentsFragmentEvent());
    }

}
