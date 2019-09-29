package app.core.checkout.delay;


import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface DelayCheckoutInterface {
    Observable<CheckInResponse> post();
    Observable<EmptyResponse> delete();

}
