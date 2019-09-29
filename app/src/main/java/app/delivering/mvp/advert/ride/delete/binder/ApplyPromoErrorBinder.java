package app.delivering.mvp.advert.ride.delete.binder;


import android.app.Activity;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import app.core.uber.delete.activity.interactor.DeleteUberInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.advert.init.events.AdvertVideoOnStartEvent;
import app.delivering.mvp.advert.ride.delete.events.OnApplyPromoErrorEvent;
import app.delivering.mvp.advert.ride.start.binder.subbinder.StartUberAppSubBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.dialog.RxDialogHandler;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ApplyPromoErrorBinder extends BaseBinder {
    private final RxDialogHandler rxDialogHandler;
    private final DeleteUberInteractor deleteUberInteractor;
    private final InitExceptionHandler initExceptionHandler;
    private final StartUberAppSubBinder startUberAppSubBinder;
    private ReplaySubject<DeleteUberResponse> replaySubject;

    public ApplyPromoErrorBinder(BaseActivity activity) {
        super(activity);
        rxDialogHandler = new RxDialogHandler(activity);
        deleteUberInteractor = new DeleteUberInteractor(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        startUberAppSubBinder = new StartUberAppSubBinder(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyPromoError(OnApplyPromoErrorEvent event) {
        rxDialogHandler.showTwoButtonsWithTitle(R.string.warning, R.string.promo_error_loading, R.string.no_cancel, R.string.yes_continue)
                .concatMap(this::selectFlow)
                .subscribe(replaySubject);
    }

    private Observable<DeleteUberResponse> selectFlow(boolean isOk) {
        if (isOk)
            return Observable.just(new DeleteUberResponse(false));
        else
            return deleteUberInteractor.process(new DeleteUberRequest())
                    .map(result -> new DeleteUberResponse(true));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(AdvertVideoOnStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void show(DeleteUberResponse deleteUberResponse) {
        resetState();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
        if (!deleteUberResponse.isOk())
            startUberAppSubBinder.start();
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> onApplyPromoError(new OnApplyPromoErrorEvent()));
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new AdvertVideoOnStartEvent());
    }

}
