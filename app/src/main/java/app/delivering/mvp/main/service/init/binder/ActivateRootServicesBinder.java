package app.delivering.mvp.main.service.init.binder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.authenticator.login.facebook.events.CheckBluetoothEvent;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.main.init.presenter.BluetoothStateListener;
import app.delivering.mvp.main.service.beacon.events.ActivateBeaconServiceEvent;
import app.delivering.mvp.main.service.feedback.events.CheckOutEvent;
import app.delivering.mvp.main.service.init.events.ActivateRootServicesEvent;
import app.delivering.mvp.main.service.init.model.ActivateServicesResult;
import app.delivering.mvp.main.service.init.presenter.ActivateRootServicesPresenter;
import app.delivering.mvp.main.service.init.presenter.CheckUpdateGcmTokenPresenter;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.android.schedulers.AndroidSchedulers;

public class ActivateRootServicesBinder extends BaseBinder {
    private final CheckUpdateGcmTokenPresenter gcmTokenPresenter;
    private ActivateRootServicesPresenter rootServicesPresenter;
    private final BluetoothStateListener bluetoothStateListener;


    public ActivateRootServicesBinder(BaseActivity activity) {
        super(activity);
        rootServicesPresenter = new ActivateRootServicesPresenter(activity);
        bluetoothStateListener = rootServicesPresenter;
        gcmTokenPresenter = new CheckUpdateGcmTokenPresenter(activity);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getCheckBluetoothEvent(CheckBluetoothEvent checkBluetoothEvent) {
        EventBus.getDefault().removeStickyEvent(checkBluetoothEvent);
        bluetoothStateListener.setUpBluetoothScanner();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(ActivateRootServicesEvent event) {
        EventBus.getDefault().post(new ActivateBeaconServiceEvent());
        tryStartTrackingServices();
        checkGcmTokenState();
    }

    private void tryStartTrackingServices() {
      rootServicesPresenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::show, err -> { }, () -> { });
    }

    private void show(ActivateServicesResult result) {
        EventBus.getDefault().post(new ActivateBeaconServiceEvent());
        if (result.getCheckOut() != null) {
            if (result.getCheckOut().isAutoClosed())
                goToResultScreen(result);
        }
    }

    private void goToResultScreen(ActivateServicesResult result) {
        boolean isReopenTab = getActivity().getIntent().getBooleanExtra(QorumNotifier.IS_REOPEN_TAB_ACTION, false);
        if (isReopenTab){
            InitialVenueDetailModel initialVenueModel = new InitialVenueDetailModel();
            initialVenueModel.setBarId(result.getCheckOut().getVendorId());
            initialVenueModel.setShouldAutoOpenTab(true);
            getActivity().startActivity(BarDetailActivity.getIntentWithExtras(getActivity(), initialVenueModel));
        } else {
            FillCloseTabActivityModel fillCloseTabActivityModel = new FillCloseTabActivityModel();
            fillCloseTabActivityModel.setCheckInId(result.getCheckOut().getId());
            fillCloseTabActivityModel.setDiscount(result.getCheckOut().getRideDiscount() != null
                    ? result.getCheckOut().getRideDiscount().getDiscountValue() : 0.d);
            fillCloseTabActivityModel.setTabAutoClosed(true);
            fillCloseTabActivityModel.setVendorId(result.getCheckOut().getVendorId());
            getActivity().startActivity(CloseTabActivity.startCloseTabActivity(getActivity(), fillCloseTabActivityModel));
        }
    }

    private void checkGcmTokenState() {
        gcmTokenPresenter.process()
                .doOnNext(r -> QorumSharedCache.checkGCMTokenUpdates().save(BaseCacheType.BOOLEAN, false))
                .subscribe(emptyResponse -> {}, e -> { }, () -> { });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCheckOut(CheckOutEvent event) {
        EventBus.getDefault().removeStickyEvent(CheckOutEvent.class);
    }
}
