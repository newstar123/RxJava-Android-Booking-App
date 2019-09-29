package app.delivering.mvp.bars.list.refresh.binder;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.refresh.events.RefreshBarListEvent;
import app.delivering.mvp.bars.list.refresh.presenter.BarListRefreshPresenter;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.init.events.OnStartMainActivityEvent;
import app.delivering.mvp.network.events.InternetConnectedEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class RefreshBarListBinder extends BaseBinder {
    private BarListRefreshPresenter presenter;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<List<BarModel>> replaySubject;

    public RefreshBarListBinder(BaseActivity activity) {
        super(activity);
        this.presenter = new BarListRefreshPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStart(OnStartMainActivityEvent event) {
        initReplaySubject();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(RefreshBarListEvent event) {
        presenter.process().subscribe(replaySubject);
    }

    private void show(List<BarModel> response) {
        CitiesModel event = EventBus.getDefault().getStickyEvent(CitiesModel.class);
        if (event == null)
            event = new CitiesModel();
        event.setManualRefreshing(true);
        initReplaySubject();
        EventBus.getDefault().postSticky(event);
    }

    private void onError(Throwable throwable) {
        initReplaySubject();
        initExceptionHandler.showError(throwable, view -> refreshList(new RefreshBarListEvent()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionChangedEvent(InternetConnectedEvent event) {
        initExceptionHandler.dismiss();
    }

    private void initReplaySubject() {
        replaySubject = ReplaySubject.create();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError);
    }

}
