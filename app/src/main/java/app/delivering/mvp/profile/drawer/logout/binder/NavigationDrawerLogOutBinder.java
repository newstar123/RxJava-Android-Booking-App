package app.delivering.mvp.profile.drawer.logout.binder;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.dialog.CustomUpdateDialog;
import app.delivering.component.payment.list.PaymentsActivity;
import app.delivering.component.service.beacon.BeaconService;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.drawer.logout.events.LogOutCallbackEvent;
import app.delivering.mvp.profile.drawer.logout.presenter.LogOutPresenter;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NavigationDrawerLogOutBinder extends BaseBinder {
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.navigation) NavigationView headerView;
    @BindView(R.id.drawer_header_photo) ImageView headerPhoto;
    @BindView(R.id.drawer_header_number) TextView number;
    @BindView(R.id.drawer_header_name) TextView name;

    private final LogOutPresenter logOutPresenter;
    private final RxDialogHandler rxDialogHandler;
    private final CustomUpdateDialog customUpdateDialog;
    private final InitExceptionHandler initExceptionHandler;


    public NavigationDrawerLogOutBinder(BaseActivity activity) {
        super(activity);
        logOutPresenter = new LogOutPresenter(activity);
        rxDialogHandler = new RxDialogHandler(activity);
        customUpdateDialog = new CustomUpdateDialog(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
    }

    @Subscribe()
    public void LogOutCheckOpenedTabEvent(LogOutCallbackEvent event) {
        if (event.isCheckOutCallBack()) {
            getActivity().runOnUiThread(() -> {
                customUpdateDialog.show();
                customUpdateDialog.setMessage(getString(R.string.closing_tab));
            });
        } else {
            closeStateDialog();
            stopServices();
            getActivity().runOnUiThread(this::resetNavigationView);
        }
    }

    @OnClick(R.id.logging_out) public void logOut() {
        rxDialogHandler.showTwoButtonsWithTitle(R.string.menu_logout, R.string.continue_logout,
                                                R.string.cancel, R.string.ok)
                .doOnNext(isOk -> rxDialogHandler.dismissDialog())
                .filter(isOk -> isOk)
                .observeOn(Schedulers.io())
                .concatMap(isOk -> logOutPresenter.process())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> { }, this::onError, ()->{});
    }

    private void onError(Throwable throwable) {
        closeStateDialog();
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 402)
            showPaymentError();
        else
            initExceptionHandler.showError(throwable, v -> logOut());
    }

    private void closeStateDialog() {
        if (customUpdateDialog != null && customUpdateDialog.isShowing())
            customUpdateDialog.dismiss();
    }

    private void showPaymentError() {
        rxDialogHandler
                .showTwoButtonsWithoutTitle(R.string.close_tab_payment_error, R.string.change_card, R.string.pay_with_cash)
                .subscribe(isOk -> {if (!isOk) showChangePaymentCard(); }, e -> {}, () -> {});
    }

    private void showChangePaymentCard() {
        getActivity().startActivity(new Intent(getActivity(), PaymentsActivity.class));
    }

    private void stopServices() {
        Intent startIntent = new Intent(getActivity(), TabStatusForegroundService.class);
        startIntent.setAction(TabStatusForegroundService.STOP_FOREGROUND_ACTION);
        getActivity().startService(startIntent);
        Intent stopBeaconIntent = new Intent(getActivity(), BeaconService.class);
        stopBeaconIntent.setAction(BeaconService.STOP_FOREGROUND_ACTION);
        getActivity().startService(stopBeaconIntent);
    }

    private void resetNavigationView() {
        if (drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (headerView != null) {
            headerPhoto.setBackground(null);
            name.setText("");
            number.setText("");
        }
    }
}
