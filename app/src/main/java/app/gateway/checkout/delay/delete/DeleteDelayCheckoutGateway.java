package app.gateway.checkout.delay.delete;


import app.core.payment.regular.model.EmptyResponse;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface DeleteDelayCheckoutGateway {

    @DELETE("v2//patrons/{patron_id}/checkins/{checkin_id}/delay")
    Observable<EmptyResponse> put(@Header("Authorization") String token,
                                  @Path("patron_id") long userId,
                                  @Path("checkin_id") long checkinId);
}
