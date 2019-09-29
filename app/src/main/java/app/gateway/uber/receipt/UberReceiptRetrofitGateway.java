package app.gateway.uber.receipt;

import app.core.uber.mock.ride.entity.UberReceiptResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;


public interface UberReceiptRetrofitGateway {
    @GET("/v1.2/requests/{request_id}/receipt")
    Observable<UberReceiptResponse> get(@Header("Authorization") String token,
                                        @Path("request_id") String requestId);
}
