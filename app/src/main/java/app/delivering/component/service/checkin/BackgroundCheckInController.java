package app.delivering.component.service.checkin;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.util.HashMap;

import app.CustomApplication;
import app.R;
import app.core.bars.list.get.entity.BarModel;
import app.core.checkin.auto.AutoCheckInInteractor;
import app.core.checkin.auto.entity.AutoCheckInResult;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.post.entity.EmailVerificationException;
import app.core.checkout.delay.DelayCheckoutInteractor;
import app.core.checkout.entity.PhoneVerificationException;
import app.core.payment.get.entity.NoPaymentException;
import app.delivering.component.service.beacon.BeaconService;
import app.delivering.component.service.beacon.broadcast.RangeCheckInReceiver;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.qamode.log.LogToFileHandler;
import retrofit2.adapter.rxjava.HttpException;

public class BackgroundCheckInController implements ControlCheckInInterface {
    private final static int TICKET_NAME_ERROR = 409;
    private final static int NOT_ENOUGH_FOUNDS_ERROR = 402;
    private final AutoCheckInInteractor checkInInteractor;
    private final DelayCheckoutInteractor checkoutInteractor;

    public BackgroundCheckInController() {
        checkInInteractor = new AutoCheckInInteractor(getContext());
        checkoutInteractor = new DelayCheckoutInteractor(getContext());
    }

    @Override
    public void activateCheckInScanner() {
        Intent startIntent = new Intent(getContext(), BeaconService.class);
        startIntent.setAction(BeaconService.START_FOREGROUND_ACTION);
        getContext().startService(startIntent);
        LogToFileHandler.addLog("activate BeaconsCheckInScanner");
    }

    @Override
    public void stopCheckInScanner() {
        Intent stopIntent = new Intent(getContext(), BeaconService.class);
        stopIntent.setAction(BeaconService.STOP_FOREGROUND_ACTION);
        getContext().startService(stopIntent);
        LogToFileHandler.addLog("stop BeaconsCheckInScanner");
    }

    @Override
    public void onCheckIn(GetCheckInsResponse response) {
        Intent checkInIntent = new Intent(RangeCheckInReceiver.ON_CHECK_IN_ACTION);
        checkInIntent.putExtra(RangeCheckInReceiver.CHECK_IN_KEY, (Parcelable) response);
        getContext().sendBroadcast(checkInIntent);
        //TODO: start auto-update service
        LogToFileHandler.addLog("Start ranging for Checkin - " + response.getId() + " in the " + response.getVendor().getName());
    }

    @Override
    public void onCheckOut(GetCheckInsResponse response) {
        sendCheckOutNotification(response);
        stopGeoPlaceRanging();
        LogToFileHandler.addLog("Stop ranging for Checkin - " + response.getId() + " in the " + response.getVendor().getName() + ". Tab was closed");
        //TODO: stop auto-update service
    }

    private void stopGeoPlaceRanging() {
        Intent checkInIntent = new Intent(RangeCheckInReceiver.ON_CHECK_OUT_ACTION);
        getContext().sendBroadcast(checkInIntent);
    }

    private void sendCheckOutNotification(GetCheckInsResponse checkIn) {
        HashMap map = new HashMap();
        map.put(QorumNotifier.VENDOR_ID, checkIn.getVendorId());
        if (checkIn.getBillItems() == null || checkIn.getBillItems().isEmpty()){
            QorumNotifier.notify(getContext(), NotificationType.TICKET_CLOSED_EMPTY, map);
        } else {
            QorumNotifier.notify(getContext(), NotificationType.TAB_AUTO_CLOSED, map);
        }
    }

    @Override
    public void onCheckOutRadiusCrossed(boolean isInRadius, PendingIntent pIntent, GetCheckInsResponse response) {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - user had crossed check out radius " + isInRadius);
        if (isInRadius){
            checkoutInteractor.delete().subscribe(success -> LogToFileHandler.addLog("Auto-check out delete success"),
                    e->{}, ()->{});
        } else {
            checkoutInteractor.post().subscribe(checkInResponse -> {
                onCheckOut(checkInResponse.getCheckin());
                LogToFileHandler.addLog("Auto-check out success");
            }, e->onAutoCheckOutError(e, response.getVendor().getName()), ()->{});
        }
    }

    private void onAutoCheckOutError(Throwable e, String barName) {
        LogToFileHandler.addLog("Auto-check out - error - " + e.getMessage());
        if (e instanceof HttpException && ((HttpException) e).code() == 402) {
            HashMap map = new HashMap();
            map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, barName);
            QorumNotifier.notify(CustomApplication.get(), NotificationType.DECLINED_PAYMENT, map);
        }
        else if (e instanceof PhoneVerificationException) {
            QorumNotifier.notify(CustomApplication.get(), NotificationType.PHONE_VERIFICATION, new HashMap());
        } else {
            if (e instanceof HttpException && ((HttpException) e).code() != 204) {
                HashMap<String, String> map = new HashMap<>();
                map.put(QorumNotifier.BEACON_ERROR_MESSAGE, getContext().getString(R.string.close_tab_error_message));
                QorumNotifier.notify(CustomApplication.get(), NotificationType.BEACON_ERROR, map);
            }
        }
    }

    @Override
    public void onCheckInRadiusCrossed(boolean isInRadius, BarModel model) {
        LogToFileHandler.addLog("onCheckInRadiusCrossed for - " + model.getName() + model.getId());
        checkInInteractor.process(model)
                .subscribe(this::onAutoCheckInSuccess, e -> onAutoCheckInError(e, model), () -> {
                    LogToFileHandler.addLog("Auto-checkin for - " + model.getName() + " was stopped");
                });
    }

    private void onAutoCheckInSuccess(AutoCheckInResult result) {
        LogToFileHandler.addLog("onAutoCheckInSuccess. Settings - " + result.isAutoOpeningSettingOn());
        HashMap map = new HashMap();
        if (result.isAutoOpeningSettingOn()) {
            LogToFileHandler.addLog("onAutoCheckInSuccess. Settings - " + result.isAutoOpeningSettingOn());
            map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, result.getCheckIn().getCheckin().getVendor().getName());
            map.put(QorumNotifier.VENDOR_ID, result.getCheckIn().getCheckin().getVendor().getId());
            map.put(QorumNotifier.CHECK_IN_ID, result.getCheckIn().getCheckin().getId());
            QorumNotifier.notify(CustomApplication.get(), NotificationType.BEACON_TAB_OPENED, map);
            onCheckIn(result.getCheckIn().getCheckin());
        } else {
            map.put(QorumNotifier.AUTO_CHECK_IN_SETTINGS_STATE, false);
            map.put(QorumNotifier.VENDOR_ID, result.getVenue().getId());
            QorumNotifier.notify(CustomApplication.get(), NotificationType.AUTO_CHECK_IN_SETTINGS, map);
        }
    }

    private void onAutoCheckInError(Throwable e, BarModel model) {
        LogToFileHandler.addLog("Auto-check in error - " + e.getMessage());
        HashMap map = new HashMap();
        map.put(QorumNotifier.VENDOR_ID, model.getId());
        if (e instanceof HttpException) {
            map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, model.getName());
            if (((HttpException) e).code() == TICKET_NAME_ERROR){
                QorumNotifier.notify(CustomApplication.get(), NotificationType.BEACONS_TAB_OPENING_ERROR_409, map);
            } else if (((HttpException) e).code() == NOT_ENOUGH_FOUNDS_ERROR) {
                QorumNotifier.notify(CustomApplication.get(), NotificationType.BEACONS_TAB_OPENING_ERROR_402, map);
            }
        } else if (e instanceof NoPaymentException) {
            QorumNotifier.notify(CustomApplication.get(), NotificationType.PAYMENT_METHOD_ERROR, map);
        } else if (e instanceof EmailVerificationException){
            QorumNotifier.notify(CustomApplication.get(), NotificationType.EMAIL_VERIFICATION_ERROR, map);
        }
    }

    private Context getContext(){ return CustomApplication.get(); }
}
