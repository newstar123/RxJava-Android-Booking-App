package app.delivering.mvp.ride.order.init.tobar.binder;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.init.tobar.binder.subbinder.ViewPagerTypeChangeSubBinder;
import app.delivering.mvp.ride.order.init.tobar.model.ToBarAddressModel;
import app.delivering.mvp.ride.order.init.tobar.presenter.ToBarAddressPresenter;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;


public class ToBarInitBinder extends BaseBinder {
    private final ToBarAddressPresenter toBarAddressPresenter;
    private final ViewPagerTypeChangeSubBinder viewPagerTypeChangeSubBinder;
    private ReplaySubject<ToBarAddressModel> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    @BindView(R.id.order_ride_bar) TextView orderRideBarTextView;
    @BindView(R.id.root_order_ride_bar) View rootOrderRideBarView;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderRidePickUpEditText;

    public ToBarInitBinder(BaseActivity activity) {
        super(activity);
        toBarAddressPresenter = new ToBarAddressPresenter(activity);
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(getActivity());
        viewPagerTypeChangeSubBinder = new ViewPagerTypeChangeSubBinder(getActivity());
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        if (getRideType() == InitialRideType.TO_THE_VENUE) {
            orderRidePickUpEditText.setVisibility(View.GONE);
            rootOrderRideBarView.setVisibility(View.VISIBLE);
            startToBarFlow();
            moveRootOrderRideBarView();
            moveOrderRideBarEditText();
        }
    }

    private void moveRootOrderRideBarView() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootOrderRideBarView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.separator_view);
        layoutParams.setMargins(0, -(int) getActivity().getResources().getDimension(R.dimen.dip3), 0, 0);
        rootOrderRideBarView.setLayoutParams(layoutParams);
    }

    private void moveOrderRideBarEditText() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderRideAddressEditText.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW,  R.id.separator_view);
        layoutParams.setMargins(12, -(int) getActivity().getResources().getDimension(R.dimen.dip48), 0, 0);
        orderRideAddressEditText.setLayoutParams(layoutParams);
    }

    private InitialRideType getRideType() {
        int value = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }

    private void startToBarFlow() {
        showProgress();
        long barId = getActivity()
                .getIntent()
                .getLongExtra(BarListItemClickBinder.DETAIL_BAR_ID, 0);
        toBarAddressPresenter.process(barId).subscribe(replaySubject);
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

    private void show(ToBarAddressModel model) {
        resetState();
        viewPagerTypeChangeSubBinder.apply();
        BarModel barModel = model.getBarModel();
        String name = barModel.getName();
        orderRideBarTextView.setText(name);
        LatLng barLatLng = new LatLng(barModel.getLatitude(), barModel.getLongitude());
        orderRideBarTextView.setTag(barLatLng);
        EventBus.getDefault().post(model.getApplyAddressEvent());
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> afterViewsBounded());
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
