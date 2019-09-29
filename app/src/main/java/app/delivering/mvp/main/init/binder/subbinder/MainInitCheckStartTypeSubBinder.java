package app.delivering.mvp.main.init.binder.subbinder;

import android.content.Intent;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;

public class MainInitCheckStartTypeSubBinder {

    public static void checkStartIntentType(BaseActivity activity){
        Intent intent = activity.getIntent();
        boolean isCheckinIntent = activity.getIntent().getBooleanExtra(TabStatusForegroundService.VIEW_CHECK_IN, false);
        intent.putExtra(TabStatusForegroundService.VIEW_CHECK_IN, isCheckinIntent);
        if (isCheckinIntent) {
            activity.getIntent().putExtra(TabStatusForegroundService.VIEW_CHECK_IN, false);
            InitialTabActivityModel model = new InitialTabActivityModel();
            model.setCheckInId(activity.getIntent().getLongExtra(QorumNotifier.CHECK_IN_ID, 0));
            model.setBarName(activity.getIntent().getStringExtra(QorumNotifier.CHECK_IN_VENDOR_NAME));
            model.setPhoneVerificationNeeded(activity.getIntent().getBooleanExtra(QorumNotifier.PHONE_VERIFICATION, false));
            model.setBarId(activity.getIntent().getLongExtra(QorumNotifier.VENDOR_ID, 0));
            TabActivity.launch(activity, model);
        }
        boolean isBeaconIntent = activity.getIntent().getBooleanExtra(QorumNotifier.BEACON_CHECK_IN_OK_ACTION, false);
        intent.putExtra(QorumNotifier.NOTIFICATION_OPEN_TAB, isBeaconIntent);
        if (isBeaconIntent) {
            QorumNotifier.clearNotification(activity, NotificationType.BEACON_TAB_OPENING);
            replaceIntent(intent, activity);
        }
        if (activity.getIntent().getBooleanExtra(QorumNotifier.IS_REOPEN_TAB_ACTION, false))
            QorumNotifier.clearNotification(activity, NotificationType.TICKET_NOT_FOUND_ERROR);
    }

    private static void replaceIntent(Intent intent, BaseActivity activity) {
        activity.getIntent().putExtra(QorumNotifier.BEACON_CHECK_IN_OK_ACTION, false);
        intent.setClass(activity, BarDetailActivity.class);
        InitialVenueDetailModel initialVenueModel = new InitialVenueDetailModel();
        initialVenueModel.setShouldAutoOpenTab(true);
        long id = activity.getIntent().getLongExtra(BarListItemClickBinder.DETAIL_BAR_ID, 0);
        initialVenueModel.setBarId(id);
        boolean autoCheckInSettingsState = activity.getIntent().getBooleanExtra(QorumNotifier.AUTO_CHECK_IN_SETTINGS_STATE, false);
        if (autoCheckInSettingsState){
            QorumSharedCache.checkAutoCheckInSettings().save(BaseCacheType.BOOLEAN, true);
            QorumNotifier.clearNotification(activity, NotificationType.toType(intent.getStringExtra(QorumNotifier.NOTIFICATION_KEY)));
        }
        intent = BarDetailActivity.updateIntent(intent, initialVenueModel);
        if (intent.getBooleanExtra(QorumNotifier.CHANGE_PAYMENT_METHOD, false))
            intent.putExtra(QorumNotifier.CHANGE_PAYMENT_METHOD, activity.getIntent().getBooleanExtra(QorumNotifier.CHANGE_PAYMENT_METHOD, false));
        if (intent.getBooleanExtra(QorumNotifier.EMAIL_VERIFICATION_ERROR, false))
            intent.putExtra(QorumNotifier.EMAIL_VERIFICATION_ERROR, activity.getIntent().getBooleanExtra(QorumNotifier.EMAIL_VERIFICATION_ERROR, false));
        activity.startActivity(intent);
    }
}
