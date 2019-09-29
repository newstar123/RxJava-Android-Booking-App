package app.delivering.mvp.tab.close.ride.uber;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.core.ride.delayed.discount.model.DiscountUpdatesModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.tab.advert.presenter.AdvertImagePresenter;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class TabClosedUberButtonBinder extends BaseBinder {
    @BindView(R.id.word_free) TextView wordFree;
    @BindView(R.id.tab_uber_max_discount) TextView discount;
    private final RxDialogHandler dialogHandler;
    private final AdvertImagePresenter advertImagePresenter;
    private double discountValue;
    private long vendorId;

    public TabClosedUberButtonBinder(BaseActivity closeTabActivity) {
        super(closeTabActivity);
        dialogHandler = new RxDialogHandler(closeTabActivity);
        advertImagePresenter = new AdvertImagePresenter(getActivity());
        FillCloseTabActivityModel model = ((CloseTabActivity)closeTabActivity).getCloseTabActivityModel();
        discountValue = model.getDiscount();
        vendorId = model.getVendorId();
    }

    @Override public void afterViewsBounded() {
        advertImagePresenter.process(discountValue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUpDiscount, err -> {}, () -> {});
    }

    private void setUpDiscount(DiscountUpdatesModel discountModel) {
        if (discountModel.getRideDiscountVal() > 0) {
                wordFree.setVisibility(View.VISIBLE);
                discount.setVisibility(View.VISIBLE);
                discount.setText(String.format(getString(R.string.dollars_value_max), discountModel.getRideDiscountVal()));
        }
    }

    @OnClick(R.id.uber_call_button) void callUber() {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(getActivity(), OrderRideActivity.class);
            intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, vendorId);
            intent.putExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, discountValue);
            intent.putExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, InitialRideType.FROM_THE_VENUE.getValue());
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
