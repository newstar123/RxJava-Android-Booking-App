package app.delivering.mvp.bars.list.freeride.binder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.checkin.user.get.entity.RideDiscount;
import app.core.ride.delayed.entity.DelayedRidesResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.coach.freeride.FreeRideCoachMarkFragment;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.freeride.events.UpdateFreeRideListEvent;
import app.delivering.mvp.bars.list.freeride.presenter.GetFreeRidesPresenter;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class FreeRideMarkBinder extends BaseBinder {
    @BindView(R.id.free_ride_mark) FrameLayout freeRideMark;

    private GetFreeRidesPresenter presenter;
    private final RxDialogHandler dialogHandler;

    public FreeRideMarkBinder(BaseActivity activity) {
        super(activity);
        presenter = new GetFreeRidesPresenter(activity);
        dialogHandler = new RxDialogHandler(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFreeRide(UpdateFreeRideListEvent event) {
        presenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(response -> show(response, event.isFreeRideActivated()),
                        e -> freeRideMark.setVisibility(View.GONE), ()->{});
    }

    private void show(DelayedRidesResponse response, boolean isFreeRideActivated) {
        if (response.getTabsWithFreeRides().size() > 0){
            freeRideMark.setVisibility(View.VISIBLE);
            freeRideMark.setOnClickListener(v -> validateUber(response.getTabsWithFreeRides().get(0).getRideDiscount()));
            tryToShowCoachMark(response.isFreeRideMarkAlreadyShown());
        } else {
            freeRideMark.setVisibility(View.GONE);
        }
        if (isFreeRideActivated && response.getTabsWithFreeRides().size() > 0)
            validateUber(response.getTabsWithFreeRides().get(0).getRideDiscount());
    }

    private void tryToShowCoachMark(boolean isCoachMarkAlreadyShown) {
        if (!isCoachMarkAlreadyShown) {
            getActivity().start(new FreeRideCoachMarkFragment());
        }
    }

    private void validateUber(RideDiscount discount) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(getActivity(), OrderRideActivity.class);
            intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, 0);
            intent.putExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, discount.getDiscountValue());
            intent.putExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, InitialRideType.CUSTOM.getValue());
            getActivity().startActivityForResult(intent, OrderRideActivity.ORDER_RIDE_REQUEST);
        } catch (PackageManager.NameNotFoundException e) {
            showDialog();
        }
    }

    private void showDialog() {
        dialogHandler.showTwoButtonsWithTitle(R.string.uber_required_title, R.string.uber_required_message, R.string.cancel, R.string.ok)
                .filter(isOk -> isOk)
                .doOnNext(aBoolean -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.uber_app_url)));
                    getActivity().startActivityForResult(intent, TabActivity.INSTALL_UBER_KEY);
                })
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(r -> {}, Throwable::printStackTrace);
    }
}
