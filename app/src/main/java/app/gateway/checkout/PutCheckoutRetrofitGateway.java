package app.gateway.checkout;


import app.core.payment.regular.model.EmptyResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PutCheckoutRetrofitGateway {

    @PUT("v2/patrons/{id}/checkins/{cid}/checkout")
    Observable<EmptyResponse> put(@Header("Authorization") String token,
                                          @Path("id") long userId,
                                          @Path("cid") long checkinId,
                                          @Body EmptyResponse empty);
}
