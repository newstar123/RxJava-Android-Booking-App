package app.gateway.payment.add;


import app.core.payment.add.entity.AddPaymentTokenModel;
import app.core.payment.get.entity.GetPaymentCardModel;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface AddPaymentRetrofitGateway {
    @POST("v2/patrons/{id}/cc") Observable<GetPaymentCardModel> post(@Header("Authorization") String token,
                                                                     @Path("id") long userId,
                                                                     @Body AddPaymentTokenModel model);
}
