package app.delivering.mvp.bars.detail.init.get.binder;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.authenticator.init.events.OnFinishAuthenticatorActivityEvent;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.get.presenter.GetBarDetailPresenter;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class GetBarDetailBinder extends BaseBinder {
    @BindView(R.id.bar_detail_progress) MaterialProgressBar progressBar;
    private final InitExceptionHandler initExceptionHandler;
    private GetBarDetailPresenter detailPresenter;
    private final long barId;

    public GetBarDetailBinder(BaseActivity activity) {
        super(activity);
        barId = ((BarDetailActivity)getActivity()).getInitialModel().getBarId();
        detailPresenter = new GetBarDetailPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        load();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void reloadBarDetail(OnFinishAuthenticatorActivityEvent reloadEvent) {
        EventBus.getDefault().removeStickyEvent(reloadEvent);
        if (progressBar != null)
            load();
    }

    private void load() {
        showProgress();
        detailPresenter.process(barId)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::show, this::onError);
    }

    private void show(BarDetailModel barModel) {
        hideProgress();
        EventBus.getDefault().post(barModel);
    }

    private void onError(Throwable throwable) {
        hideProgress();
        initExceptionHandler.showError(throwable, view -> load());
    }
}
