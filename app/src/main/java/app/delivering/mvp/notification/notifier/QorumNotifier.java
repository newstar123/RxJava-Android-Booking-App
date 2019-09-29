package app.delivering.mvp.notification.notifier;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import app.R;
import app.core.checkout.delay.DelayCheckoutInteractor;
import app.delivering.component.main.MainActivity;
import app.delivering.component.service.beacon.BeaconService;
import app.delivering.component.service.beacon.broadcast.BackgroundBroadcastReceiver;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;
import app.delivering.mvp.dialog.base.events.HideNotificationDialogEvent;
import app.delivering.mvp.dialog.base.events.ShowNotificationDialogEvent;
import app.qamode.log.LogToFileHandler;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class QorumNotifier {
    //params key
    public static final String CHECK_IN_VENDOR_NAME = "check_in_vendor_name";
    public static final String BEACON_ERROR_TITLE = "beacon_error_title";
    public static final String BEACON_ERROR_MESSAGE = "beacon_error_message";
    public static final String MESSAGE = "message";
    public static final String BEACON_CHECK_IN_OK_ACTION = "beacon_check_in_ok_action";
    public static final String IS_REOPEN_TAB_ACTION = "is_reopen_tab_action";
    public static final String VENDOR_ID = "vendor_id_key";
    public static final String CHECK_IN_ID = "check_in_id_key";
    public static final String CHANGE_PAYMENT_METHOD = "change_payment_method";
    public static final String PHONE_VERIFICATION = "phone_verification";
    public static final String NOTIFICATION_OPEN_TAB = "NOTIFICATION_OPEN_TAB";
    public static final String NOTIFICATION_VIEW_TAB = "NOTIFICATION_VIEW_TAB";
    public static final String TIME_TO_RIDE_DISCOUNT = "TIME_TO_RIDE_DISCOUNT";
    public static final String AUTO_CHECK_IN_SETTINGS_STATE = "AUTO_CHECK_IN_SETTINGS_STATE";
    public static final String NOTIFICATION_KEY = "notification_key";
    public static final String EMAIL_VERIFICATION_ERROR = "mvp.notification.notifier.EMAIL_VERIFICATION_ERROR";


    public static void notify(Context context, NotificationType type, HashMap params) {
        LogToFileHandler.addLog("QorumNotifier - notify" + type.name());

        if (isAppInForeground() && type != NotificationType.BEACON_TAB_OPENING)
            notifyByDialog(type, params);
        else
            notifyByMessage(context, type, params);
    }

    private static boolean isAppInForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
    }

    private static void notifyByMessage(Context context, NotificationType type, HashMap params) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            Notification notification = getNotification(context, type, params);

            if (notification != null) {
                addChannel(context, type.getChanelID());
                notificationManager.notify(type.getNotificationID(), notification);
            }
        }
    }

    public static Notification getNotification(Context context, NotificationType type, HashMap params) {
        NotificationCompat.Builder builder = getNotificationBuilder(context, type);
        LogToFileHandler.addLog("QorumNotifier - getNotification" + type.name());
        switch (type) {
            case BEACON_ERROR: // without dialog
                builder.setContentTitle(TextUtils.isEmpty((String) params.get(BEACON_ERROR_TITLE)) ? context.getString(R.string.scanning_sevice_stopped) : (String) params.get(BEACON_ERROR_TITLE));
                builder.setContentText(TextUtils.isEmpty((String) params.get(BEACON_ERROR_MESSAGE)) ? context.getString(R.string.pleace_restart_beacon_service) : (String) params.get(BEACON_ERROR_MESSAGE));
                builder.setAutoCancel(true);
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
                if (TextUtils.isEmpty((String) params.get(BEACON_ERROR_MESSAGE)))
                    builder.setDeleteIntent(getCloseBeaconErrorsIntent(context, type));
                break;
            case BEACON_SCANNING: // without dialog
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
                builder.setOngoing(true);
                builder.setContentTitle(context.getString(R.string.beacon_service_run_title));
                builder.setContentText(context.getString(R.string.beacon_service_run_message));
                builder.setAutoCancel(false);
                builder.addAction(R.drawable.icon_stop, context.getString(R.string.stop_service), getCloseBeaconScanningIntent(context, type));
                break;
            case DECLINED_PAYMENT:
                String vendor = (String) params.get(CHECK_IN_VENDOR_NAME);
                builder.setContentTitle(vendor);
                builder.setContentIntent(getViewTabIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getText(R.string.pre_auth_founds_error)));
                break;
            case BEACONS_TAB_OPENING_ERROR_409:
                builder.setContentTitle((String) params.get(CHECK_IN_VENDOR_NAME));
                builder.setContentIntent(getOpenTabPendingIntent(context, type, params, false));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getText(R.string.something_wrong_notification)));
                break;
            case BEACON_TAB_OPENING:
                String vendorName = (String) params.get(CHECK_IN_VENDOR_NAME);
                builder.setContentTitle(vendorName);
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(context.getString(R.string.would_you_like_to_check_in), vendorName)));
                builder.setAutoCancel(true);
                builder.setOnlyAlertOnce(true);
                builder.setDeleteIntent(getCloseBeaconNotificationIntent(context, type));
                builder.setContentIntent(getOpenTabPendingIntent(context, type, params, false));
                break;
            case BEACON_TAB_OPENED:
                builder.setContentTitle((String) params.get(CHECK_IN_VENDOR_NAME));
                builder.setContentIntent(getViewTabIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(context.getString(R.string.beacon_auto_open_tab), (String) params.get(CHECK_IN_VENDOR_NAME))));
                break;
            case TAB_CLOSED:
                builder.setContentTitle((String) params.get(CHECK_IN_VENDOR_NAME));
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.beacon_auto_close_tab)));
                break;
            case TAB_AUTO_CLOSE_TIMER:
                builder.setContentTitle(String.format(context.getString(R.string.headsup_youve_exited_venue),
                                (String) params.get(CHECK_IN_VENDOR_NAME)));
                builder.setContentIntent(getViewTabIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(context.getString(R.string.re_enter_the_venue_warning),
                                String.valueOf(DelayCheckoutInteractor.DELAY_IN_MINUTES))));
                break;
            case TAB_AUTO_CLOSING_STOPPED:
                builder.setContentIntent(getViewTabIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(context.getString(R.string.re_enter_the_venue_message),
                        (String) params.get(CHECK_IN_VENDOR_NAME))));
                break;
            case PHONE_VERIFICATION:
                builder.setContentTitle(context.getString(R.string.profile_verification));
                builder.setContentIntent(getVerifyPhoneNumberIntent(context, type));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.please_verify_phone)));
                break;
            case PAYMENT_METHOD_ERROR:
                builder.setContentTitle(context.getString(R.string.open_tab_dialog_title));
                builder.setContentIntent(getChangePaymentIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.need_payment_message)));
                break;
            case BEACONS_TAB_OPENING_ERROR_402:
                builder.setContentTitle(context.getString(R.string.tab_failed_to_open));
                builder.setContentIntent(getChangePaymentIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getText(R.string.please_change_payment_card)));
                break;
            case EMAIL_VERIFICATION_ERROR:
                builder.setContentTitle(context.getString(R.string.tab_failed_to_open));
                builder.setContentIntent(getVerifyEmailIntent(context, type, params));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getText(R.string.please_verify_email)));
                break;
            case TAB_AUTO_CLOSED:
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
                builder.setAutoCancel(true);
                builder.setDeleteIntent(getCloseBeaconErrorsIntent(context, type));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.tab_auto_closed)));
                break;
            case TICKET_CLOSED_EMPTY:
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.ticket_not_found)));
                builder.setAutoCancel(true);
                builder.setDeleteIntent(getCloseBeaconErrorsIntent(context, type));
                builder.setContentIntent(getReOpenTabPendingIntent(context, NotificationType.TICKET_NOT_FOUND_ERROR, params));
                break;
            case TICKET_NOT_FOUND_ERROR:
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.ticket_closed_empty_error)));
                builder.setAutoCancel(true);
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
            case POS_ERROR_MESSAGE:
                builder.setContentTitle(context.getString(R.string.pos_error_500_n_title));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText((String)params.get(MESSAGE)));
                builder.setAutoCancel(true);
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
                break;
            case RIDE_DISCOUNT_WAS_ATTACHED:
                builder.setContentTitle(context.getString(R.string.headsup_with_exclamation));
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(context.getString(R.string.free_ride_already_added),
                        (params.get(TIME_TO_RIDE_DISCOUNT) == null) ? 60 : String.valueOf((int)params.get(TIME_TO_RIDE_DISCOUNT) / 60))));
                builder.setAutoCancel(true);
                builder.setContentIntent(getRestartAppPendingIntent(context, type));
                break;
            case AUTO_CHECK_IN_SETTINGS:
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.auto_check_in_settings)));
                builder.setAutoCancel(true);
                builder.addAction(0, context.getString(R.string.dismiss), cancelCurrent(context, type));
                builder.addAction(0, context.getString(R.string.allow), getOpenTabPendingIntent(context, type, params, true));
                break;
            case DEF:
                if (TextUtils.isEmpty((String) params.get(MESSAGE))) {
                    return null;
                } else {
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText((String) params.get(MESSAGE)));
                    builder.setContentIntent(getRestartAppPendingIntent(context, type));
                }
                break;
        }

        builder.setChannelId(type.getChanelID());
        return builder.build();
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context, NotificationType type) {
        NotificationCompat.Builder rootBuilder = new NotificationCompat.Builder(context, type.getChanelID())
                .setContentTitle(context.getText(R.string.app_name))
                .setSmallIcon(R.drawable.icon_logo)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setColor(context.getResources().getColor(R.color.accent));
        if (type == NotificationType.BEACON_SCANNING || type == NotificationType.BEACON_ERROR)
            return rootBuilder;
        else
            return rootBuilder
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{500, 500, 500, 500, 500});
    }

    private static PendingIntent cancelCurrent(Context context, NotificationType type) {
        Intent intent = getRootIntent(type);
        return PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getVerifyPhoneNumberIntent(Context context, NotificationType type) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.putExtra(TabStatusForegroundService.VIEW_CHECK_IN, true);
        intent.putExtra(PHONE_VERIFICATION, true);
        LogToFileHandler.addLog("QorumNotifier - getVerifyPhoneNumberIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getChangePaymentIntent(Context context, NotificationType type, HashMap params) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BEACON_CHECK_IN_OK_ACTION, true);
        intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, (long) params.get(VENDOR_ID));
        intent.putExtra(CHANGE_PAYMENT_METHOD, true);
        LogToFileHandler.addLog("QorumNotifier - getChangePaymentIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getVerifyEmailIntent(Context context, NotificationType type, HashMap params) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BEACON_CHECK_IN_OK_ACTION, true);
        intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, (long) params.get(VENDOR_ID));
        intent.putExtra(EMAIL_VERIFICATION_ERROR, true);
        LogToFileHandler.addLog("QorumNotifier - getVerifyEmailIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getOpenTabPendingIntent(Context context, NotificationType type, HashMap params, boolean autoCheckInSettingState) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BEACON_CHECK_IN_OK_ACTION, true);
        intent.putExtra(NOTIFICATION_OPEN_TAB, true);
        intent.putExtra(AUTO_CHECK_IN_SETTINGS_STATE, autoCheckInSettingState);
        intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, (long) params.get(VENDOR_ID));
        LogToFileHandler.addLog("QorumNotifier - getOpenTabPendingIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getReOpenTabPendingIntent(Context context, NotificationType type, HashMap params) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BEACON_CHECK_IN_OK_ACTION, false);
        intent.putExtra(IS_REOPEN_TAB_ACTION, true);
        intent.putExtra(BarListItemClickBinder.DETAIL_BAR_ID, (long) params.get(VENDOR_ID));
        LogToFileHandler.addLog("QorumNotifier - getReOpenTabPendingIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    
    private static PendingIntent getViewTabIntent(Context context, NotificationType type, HashMap params) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.putExtra(TabStatusForegroundService.VIEW_CHECK_IN, true);
        intent.putExtra(QorumNotifier.CHECK_IN_ID, (long) params.get(CHECK_IN_ID));
        intent.putExtra(QorumNotifier.CHECK_IN_VENDOR_NAME, (String) params.get(CHECK_IN_VENDOR_NAME));
        intent.putExtra(QorumNotifier.VENDOR_ID, (long) params.get(VENDOR_ID));
        intent.putExtra(NOTIFICATION_VIEW_TAB, true);
        LogToFileHandler.addLog("QorumNotifier - getViewTabIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getRestartAppPendingIntent(Context context, NotificationType type) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LogToFileHandler.addLog("QorumNotifier - getRestartAppPendingIntent");
        return PendingIntent.getActivity(context, type.getNotificationID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getCloseBeaconErrorsIntent(Context context, NotificationType type) {
        return PendingIntent.getBroadcast(context, 1,
                getRootIntent(type), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getCloseBeaconScanningIntent(Context context, NotificationType type) {
        LogToFileHandler.addLog("QorumNotifier - getCloseBeaconScanningIntent");
        Intent buttonIntent = new Intent(context, BeaconService.class);
        buttonIntent.setAction(BeaconService.STOP_FOREGROUND_ACTION);
        buttonIntent.putExtra(BackgroundBroadcastReceiver.NOTIFICATION_ID, type.getNotificationID());
        return PendingIntent.getService(context, 0, buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getCloseBeaconNotificationIntent(Context context, NotificationType type) {
        Intent intent = getRootIntent(type);
        intent.setClass(context, BeaconService.class);
        return PendingIntent.getService(context, type.getNotificationID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static Intent getRootIntent(NotificationType type) {
        Intent intent = new Intent();
        intent.setAction(BackgroundBroadcastReceiver.CLEAR_NOTIFICATION_FILTER_ACTION);
        intent.putExtra(NOTIFICATION_KEY, type.name());
        return intent;
    }

    private static void notifyByDialog(NotificationType type, HashMap params) {
        EventBus.getDefault().post(new ShowNotificationDialogEvent(type, params));
    }

    public static void clearNotification(Context context, NotificationType type) {
        clearMessage(context, type);
        clearDialog(type);
    }

    private static void clearMessage(Context context, NotificationType type) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                notificationManager.deleteNotificationChannel(type.getChanelID());
            else if (type != NotificationType.BEACON_SCANNING)
                notificationManager.cancel(type.getNotificationID());
        }
    }

    private static void clearDialog(NotificationType type) {
        EventBus.getDefault().post(new HideNotificationDialogEvent(type));
    }

    public static void addChannel(Context context, String channelID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && notificationManager != null){
            //check if channel already exists
            for (NotificationChannel channel : notificationManager.getNotificationChannels()) {
                if (channelID.equalsIgnoreCase(channel.getId())) return;
            }
            //else add new channel
            NotificationChannel notificationChannel;
            if (channelID.equalsIgnoreCase(NotificationType.BEACON_SCANNING.getChanelID())) {
                notificationChannel = new NotificationChannel(channelID, context.getText(R.string.app_name),
                        NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setShowBadge(false);
            } else
                notificationChannel = new NotificationChannel(channelID, context.getText(R.string.app_name),
                        NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
