package app.delivering.mvp.payment.list.init.binder;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.payment.list.adapter.PaymentsAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.payment.add.add.validate.events.ValidateAddPaymentEvent;
import app.delivering.mvp.payment.list.init.events.LoadPaymentsEvent;
import app.delivering.mvp.payment.list.init.events.OnStartPaymentsFragmentEvent;
import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;
import app.delivering.mvp.payment.list.init.presenter.PaymentsInitPresenter;
import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class PaymentsInitBinder extends BaseBinder {
    private final InitExceptionHandler initExceptionHandler;
    @BindView(R.id.payments) RecyclerView paymentsRecyclerView;
    @BindView(R.id.payments_progress)View progress;
    private final PaymentsAdapter adapter;
    private final PaymentsInitPresenter presenter;
    private ReplaySubject<List<PaymentsInitBinderModel>> replaySubject;
    private Subscription binderSubscribe;

    public PaymentsInitBinder(BaseActivity activity) {
        super(activity);
        adapter = new PaymentsAdapter();
        presenter = new PaymentsInitPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        setProgress(progress);
        setUpPaymentsRecyclerView();
        onAddPaymentEvent(new LoadPaymentsEvent());
    }

    private void setUpPaymentsRecyclerView() {
        paymentsRecyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        paymentsRecyclerView.setLayoutManager(linearLayoutManager);
        paymentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        paymentsRecyclerView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentEvent(LoadPaymentsEvent event) {
        progressState();
        process();
    }

    private void process() {
        binderSubscribe = presenter.process()
                .subscribe(replaySubject);
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

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void show(List<PaymentsInitBinderModel> paymentsInitBinderModels) {
        resetState();
        adapter.setModels(paymentsInitBinderModels);
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> EventBus.getDefault().post(new ValidateAddPaymentEvent()));
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onAddPaymentEvent(new OnStartPaymentsFragmentEvent());
    }

}
