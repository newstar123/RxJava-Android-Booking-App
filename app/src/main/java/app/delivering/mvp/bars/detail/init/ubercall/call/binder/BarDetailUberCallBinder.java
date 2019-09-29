package app.delivering.mvp.bars.detail.init.ubercall.call.binder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.init.token.entity.NoAccountException;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.action.click.events.CallUberToTheVenueEvent;
import app.delivering.mvp.bars.detail.init.ubercall.call.model.BarDetailValidateUberModel;
import app.delivering.mvp.bars.detail.init.ubercall.call.presenter.BarDetailValidateUberPresenter;
import app.delivering.mvp.bars.detail.init.ubercall.init.exceptions.GuestUberCallException;
import app.delivering.mvp.bars.detail.init.ubercall.init.exceptions.UberCallTooFarException;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.dialog.RxDialogHandler;

public class BarDetailUberCallBinder extends BaseBinder {
    private RxDialogHandler dialogHandler;
    private BarDetailValidateUberPresenter presenter;

    public BarDetailUberCallBinder(BaseActivity activity) {
        super(activity);
        dialogHandler = new RxDialogHandler(getActivity());
        presenter = new BarDetailValidateUberPresenter(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void call(CallUberToTheVenueEvent event) {
       presenter.process(new BarDetailValidateUberModel(event.getEstimationResult()))
                .subscribe(result -> validateUber(), this::showError);
    }

    private void validateUber() {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(getActivity(), OrderRideActivity.class);
            long barId = ((BarDetailActivity)getActivity()).getInitialModel().getBarId();
            intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, barId);
            getActivity().startActivityForResult(intent, OrderRideActivity.ORDER_RIDE_REQUEST);
        } catch (PackageManager.NameNotFoundException e) {
            showDialog();
        }
    }

    private void showError(Throwable e) {
        if (e instanceof UberCallTooFarException)
            showDialogMessage(R.string.too_far_for_call_uber);
        if (e instanceof NoAccountException || e instanceof GuestUberCallException)
            showDialogMessage(R.string.guest_access_uber);
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
