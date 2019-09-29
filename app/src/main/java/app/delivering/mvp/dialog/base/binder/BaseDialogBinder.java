package app.delivering.mvp.dialog.base.binder;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import app.R;
import app.core.checkout.delay.DelayCheckoutInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.main.MainActivity;
import app.delivering.component.service.beacon.broadcast.BackgroundBroadcastReceiver;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.component.tab.TabActivity;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.dialog.base.events.HideNotificationDialogEvent;
import app.delivering.mvp.dialog.base.events.ShowNotificationDialogEvent;
import app.delivering.mvp.dialog.base.model.SharedCheckInIdsModel;
import app.delivering.mvp.dialog.base.presenter.BaseDialogIDsPresenter;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import rx.Observable;

import static app.delivering.mvp.notification.notifier.QorumNotifier.CHECK_IN_VENDOR_NAME;
import static app.delivering.mvp.notification.notifier.QorumNotifier.MESSAGE;
import static app.delivering.mvp.notification.notifier.QorumNotifier.NOTIFICATION_KEY;
import static app.delivering.mvp.notification.notifier.QorumNotifier.PHONE_VERIFICATION;
import static app.delivering.mvp.notification.notifier.QorumNotifier.TIME_TO_RIDE_DISCOUNT;
import static app.delivering.mvp.notification.notifier.QorumNotifier.VENDOR_ID;

public class BaseDialogBinder extends BaseBinder {
    private RxDialogHandler dialogHandler;
    private BaseDialogIDsPresenter iDsPresenter;
    private View.OnClickListener onClickListener;


    public BaseDialogBinder(BaseActivity activity) {
        super(activity);
        dialogHandler = new RxDialogHandler(activity);
        iDsPresenter = new BaseDialogIDsPresenter(activity);
    }

    public void onShowDialogEvent(ShowNotificationDialogEvent showEvent, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        onShowDialogEvent(showEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onShowDialogEvent(ShowNotificationDialogEvent showEvent) {
        final Intent intent = setUpIntent(showEvent.getType());
        switch (showEvent.getType()) {
            case DECLINED_PAYMENT:
                dialogHandler.showTwoButtonsWithoutTitle(R.string.pre_auth_founds_error, R.string.cancel, R.string.ok)
                        .subscribe(isOpenTab -> {
                            if (isOpenTab) {
                                intent.setClass(getActivity(), MainActivity.class);
                                intent.putExtra(TabStatusForegroundService.VIEW_CHECK_IN, true);
                            }
                        }, Throwable::printStackTrace);
                break;

            case BEACONS_TAB_OPENING_ERROR_409:
                dialogHandler.showTwoButtonsWithoutTitle(R.string.something_wrong_notification, R.string.cancel, R.string.ok)
                        .subscribe(aBoolean -> {
                            if (aBoolean) openTab(showEvent);
                        }, Throwable::printStackTrace);
                break;

            case BEACON_TAB_OPENED:
                String tabOpenedMessage = String.format(getString(R.string.beacon_auto_open_tab), showEvent.getParams().get(CHECK_IN_VENDOR_NAME));
                dialogHandler.showTwoButtonsWithoutTitle(tabOpenedMessage, R.string.cancel, R.string.ok)
                        .subscribe(aBoolean -> {
                            if (aBoolean) viewTab(showEvent.getParams());
                        }, Throwable::printStackTrace);
                break;

            case TAB_AUTO_CLOSE_TIMER:
                String title = String.format(getString(R.string.headsup_youve_exited_venue),
                        (String) showEvent.getParams().get(CHECK_IN_VENDOR_NAME));
                String message = String.format(getString(R.string.re_enter_the_venue_warning),
                        String.valueOf(DelayCheckoutInteractor.DELAY_IN_MINUTES));
                dialogHandler.showOneButtonWithTitle(title, message, R.string.ok)
                        .subscribe(aBoolean -> { }, Throwable::printStackTrace);
                break;

            case TAB_AUTO_CLOSING_STOPPED:
                String reEnterMessage = String.format(getActivity().getString(R.string.re_enter_the_venue_message),
                        (String) showEvent.getParams().get(CHECK_IN_VENDOR_NAME));
                dialogHandler.showOneButtonWithoutTitle(reEnterMessage, R.string.ok)
                        .subscribe(aBoolean -> { }, Throwable::printStackTrace);
                break;

            case PHONE_VERIFICATION:
                dialogHandler.showTwoButtonsWithoutTitle(R.string.please_verify_phone, R.string.cancel, R.string.ok)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                intent.setClass(getActivity(), MainActivity.class);
                                intent.putExtra(TabStatusForegroundService.VIEW_CHECK_IN, true);
                                intent.putExtra(PHONE_VERIFICATION, true);
                                getActivity().startActivity(intent);
                            }
                        }, Throwable::printStackTrace);
                break;

            case PAYMENT_METHOD_ERROR:
                dialogHandler.showTwoButtonsWithTitle(R.string.open_tab_dialog_title, R.string.need_payment_message, R.string.cancel, R.string.add_card)
                        .subscribe(aBoolean -> {
                            if (aBoolean)
                                startAutoCheckInWithPaymentError(showEvent);
                        }, Throwable::printStackTrace);
                break;

            case BEACONS_TAB_OPENING_ERROR_402:
                dialogHandler.showTwoButtonsWithTitle(R.string.tab_failed_to_open, R.string.please_change_payment_card, R.string.cancel, R.string.change_payment_method)
                        .subscribe(aBoolean -> {
                            if (aBoolean)
                                startAutoCheckInWithPaymentError(showEvent);
                        }, Throwable::printStackTrace);
                break;

            case EMAIL_VERIFICATION_ERROR:
                dialogHandler.showTwoButtonsWithTitle(R.string.tab_failed_to_open, R.string.please_verify_email, R.string.cancel, R.string.verify_email)
                        .subscribe(aBoolean -> {
                            if (aBoolean)
                                startAutoCheckInWithEmailError(showEvent);
                        }, Throwable::printStackTrace);
                break;

            case TICKET_NOT_FOUND_ERROR:
                Observable.zip(dialogHandler.showTwoButtonsWithoutTitle(R.string.ticket_not_found, R.string.no_thanks, R.string.yes),
                        iDsPresenter.process(), (isOk, model) -> {
                            startActivity(isOk, model);
                            return isOk;
                        })
                        .subscribe(isOk->{}, e->{}, ()->{});
                break;

            case TICKET_CLOSED_EMPTY:
                dialogHandler.showOneButtonWithoutTitle(R.string.ticket_not_found, R.string.ok)
                        .subscribe(isOk -> {}, e->{}, ()->{});
                break;

            case POS_ERROR_MESSAGE:
                dialogHandler.showOneButtonWithTitle(getString(R.string.pos_error_500_n_title), (String) showEvent.getParams().get(MESSAGE), R.string.got_it)
                        .subscribe(isOk -> {}, e->{}, ()->{});
                break;

            case RIDE_DISCOUNT_WAS_ATTACHED:
                dialogHandler.showOneButtonWithTitle(getString(R.string.headsup_with_exclamation),
                        String.format(getString(R.string.free_ride_already_added),
                                (showEvent.getParams().get(TIME_TO_RIDE_DISCOUNT) == null) ? 60 : String.valueOf((int)showEvent.getParams().get(TIME_TO_RIDE_DISCOUNT) / 60)),
                        R.string.ok)
                        .subscribe(isOk -> dialogHandler.dismissDialog(), e -> { }, () -> { });
                break;

            case AUTO_CHECK_IN_SETTINGS:
                dialogHandler.showTwoButtonsWithoutTitle(R.string.auto_check_in_settings, R.string.dismiss, R.string.allow)
                        .subscribe(isOk -> {
                            if (isOk) {
                                QorumSharedCache.checkAutoCheckInSettings().save(BaseCacheType.BOOLEAN, true);
                                openTab(showEvent);
                            } else dialogHandler.dismissDialog();
                        }, onErr ->{}, ()-> {});
                break;

            case DEF: {
                LogToFileHandler.addLog("QorumNotifier - BaseDialog DEF case message: "
                        .concat((String) showEvent.getParams().get(QorumNotifier.MESSAGE)));

                if (!TextUtils.isEmpty((String) showEvent.getParams().get(QorumNotifier.MESSAGE)))
                    dialogHandler.showOneButtonWithoutTitle((String) showEvent.getParams().get(QorumNotifier.MESSAGE), R.string.ok)
                            .subscribe(isOk -> {
                                if (onClickListener != null && getActivity().getCurrentFocus() != null)
                                    onClickListener.onClick(getActivity().getCurrentFocus());
                            }, Throwable::printStackTrace);
                break;
            }
        }
    }

    private void startActivity(Boolean isOk, SharedCheckInIdsModel model) {
        if (isOk) {
            InitialVenueDetailModel initialVenueModel = new InitialVenueDetailModel();
            initialVenueModel.setBarId(model.getBarId());
            initialVenueModel.setShouldAutoOpenTab(true);
            Intent intent = BarDetailActivity.getIntentWithExtras(getActivity(), initialVenueModel);
            getActivity().startActivity(intent);
        } else {
            FillCloseTabActivityModel fillCloseTabActivityModel = new FillCloseTabActivityModel();
            fillCloseTabActivityModel.setCheckInId(model.getCheckInId());
            fillCloseTabActivityModel.setDiscount(0);
            fillCloseTabActivityModel.setTabAutoClosed(true);
            fillCloseTabActivityModel.setVendorId(model.getBarId());
            getActivity().startActivity(CloseTabActivity.startCloseTabActivity(getActivity(), fillCloseTabActivityModel));
        }
    }

    private void viewTab(HashMap params) {
        InitialTabActivityModel model = new InitialTabActivityModel();
        model.setCheckInId((long) params.get(QorumNotifier.CHECK_IN_ID));
        model.setBarName((String) params.get(QorumNotifier.CHECK_IN_VENDOR_NAME));
        model.setPhoneVerificationNeeded(false);
        model.setBarId((long) params.get(QorumNotifier.VENDOR_ID));
        TabActivity.launch(getActivity(), model);
    }

    private void openTab(ShowNotificationDialogEvent showEvent) {
        InitialVenueDetailModel initialVenueModel = new InitialVenueDetailModel();
        initialVenueModel.setBarId((long) showEvent.getParams().get(VENDOR_ID));
        initialVenueModel.setShouldOpenBySwiping(true);
        initialVenueModel.setSwipingText(getString(R.string.open_tab));
        Intent intent = BarDetailActivity.getIntentWithExtras(getActivity(), initialVenueModel);
        getActivity().startActivity(intent);
    }

    private void startAutoCheckInWithPaymentError(ShowNotificationDialogEvent showEvent) {
        InitialVenueDetailModel initialModel = getAutoOpenTabModel(showEvent);
        initialModel.setShouldChangePaymentMethod(true);
        getActivity().startActivity(BarDetailActivity.getIntentWithExtras(getActivity(), initialModel));
    }

    private void startAutoCheckInWithEmailError(ShowNotificationDialogEvent showEvent) {
        InitialVenueDetailModel initialModel = getAutoOpenTabModel(showEvent);
        initialModel.setShouldVerifyEmail(true);
        getActivity().startActivity(BarDetailActivity.getIntentWithExtras(getActivity(), initialModel));
    }

    private InitialVenueDetailModel getAutoOpenTabModel(ShowNotificationDialogEvent showEvent) {
        InitialVenueDetailModel initialModel = new InitialVenueDetailModel();
        initialModel.setBarId((long)showEvent.getParams().get(VENDOR_ID));
        initialModel.setShouldAutoOpenTab(true);
        return initialModel;
    }

    private Intent setUpIntent(NotificationType type) {
        Intent intent = new Intent();
        intent.setAction(BackgroundBroadcastReceiver.CLEAR_NOTIFICATION_FILTER_ACTION);
        intent.putExtra(NOTIFICATION_KEY, type.name());
        return intent;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onHideDialogEvent(HideNotificationDialogEvent hideEvent) {
        if (dialogHandler != null)
            dialogHandler.dismissDialog();
    }
}
