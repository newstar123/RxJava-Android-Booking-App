package app.gateway.payment.get;


import app.core.payment.get.entity.GetPaymentCardsModel;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface GetPaymentsRetrofitGateway {
    @GET("v2/patrons/{id}/cc")
    Observable<GetPaymentCardsModel> get(@Header("Authorization") String token,
                                         @Path("id") long userId);
}
