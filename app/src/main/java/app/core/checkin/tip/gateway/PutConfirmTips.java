package app.core.checkin.tip.gateway;


import app.core.checkin.tip.entity.ConfirmTipsRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import rx.Observable;

public interface PutConfirmTips {
    Observable<CheckInResponse> put(ConfirmTipsRequest request);
}
