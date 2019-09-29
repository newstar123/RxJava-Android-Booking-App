package app.gateway.payment.delete;


import app.core.payment.regular.model.EmptyResponse;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface DeletePaymentRetrofitGateway {
    @DELETE("v2/patrons/{id}/cc/{payment_token}")
    Observable<EmptyResponse> delete(@Header("Authorization") String token,
                                     @Path("id") long userId,
                                     @Path("payment_token") String paymentToken);
}
