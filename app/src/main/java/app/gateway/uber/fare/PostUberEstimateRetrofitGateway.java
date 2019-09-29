package app.gateway.uber.fare;

import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;


public interface PostUberEstimateRetrofitGateway {
    @POST("/v1.2/requests/estimate")
    Observable<RideEstimate> get(@Header("Authorization") String token,
                                 @Body RideRequestParameters requestId);
}
