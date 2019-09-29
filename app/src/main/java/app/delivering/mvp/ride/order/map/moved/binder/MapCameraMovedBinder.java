package app.delivering.mvp.ride.order.map.moved.binder;

import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.map.moved.model.MapCameraMovedModel;
import app.delivering.mvp.ride.order.map.moved.model.OnMapCameraMoved;
import app.delivering.mvp.ride.order.map.moved.presenter.MapCameraMovePresenter;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;


public class MapCameraMovedBinder extends BaseBinder {
    private final MapCameraMovePresenter mapCameraMovePresenter;
    @BindView(R.id.order_ride_confirm_address) TextView orderRideConfirmAddressTextView;
    @BindView(R.id.order_ride_start_ride) TextView startRideTextView;
    private ReplaySubject<MapCameraMovedModel> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private OnMapCameraMoved event;

    public MapCameraMovedBinder(BaseActivity activity) {
        super(activity);
        mapCameraMovePresenter = new MapCameraMovePresenter(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapCameraMoved(OnMapCameraMoved event) {
        startRideTextView.setClickable(false);
        orderRideConfirmAddressTextView.setText("");
        orderRideConfirmAddressTextView.setTag(new Object());
        progressState();
        this.event = event;
        mapCameraMovePresenter.process(event.getTarget()).subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OnOrderRideStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(MapCameraMovedModel model) {
        resetState();
        orderRideConfirmAddressTextView.setText(model.getAddressLine());
        orderRideConfirmAddressTextView.setTag(model.getAddress());
        startRideTextView.setClickable(true);
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> onMapCameraMoved(event));
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }
}
