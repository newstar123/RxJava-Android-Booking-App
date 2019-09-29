package app.gateway.payment.regular;


import app.core.payment.regular.model.EmptyResponse;
import app.core.payment.regular.model.RegularPaymentRequest;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PutRegularPaymentRetrofitGateway {
    @PUT("v2/patrons/{id}/cc")
    Observable<EmptyResponse> put(@Header("Authorization") String authToken,
                                  @Path("id") long userId,
                                  @Body RegularPaymentRequest regularPaymentRequest);
}
