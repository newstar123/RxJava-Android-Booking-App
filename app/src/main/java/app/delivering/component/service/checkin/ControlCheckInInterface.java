package app.delivering.component.service.checkin;

import android.app.PendingIntent;

import app.core.bars.list.get.entity.BarModel;
import app.core.checkin.user.get.entity.GetCheckInsResponse;

public interface ControlCheckInInterface {

    void activateCheckInScanner();
    void stopCheckInScanner();
    void onCheckIn(GetCheckInsResponse response);
    void onCheckOut(GetCheckInsResponse response);
    void onCheckOutRadiusCrossed(boolean isInRadius, PendingIntent pIntent, GetCheckInsResponse response);
    void onCheckInRadiusCrossed(boolean isInRadius, BarModel model);
}
