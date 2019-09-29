package app.gateway.uber.estimate.price;

import app.core.uber.estimate.price.entity.UberPriceEstimatesResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GetUberPriceEstimatesRetrofitGateway {
    @GET("v2/patrons/{id}/ride/uber/price") Observable<UberPriceEstimatesResponse> get(@Header("Authorization") String token,
                                                                                       @Path("id") long userId,
                                                                                       @Query("start_lat") double startLat,
                                                                                       @Query("start_lng") double startLng,
                                                                                       @Query("stop_lat") double stopLat,
                                                                                       @Query("stop_lng") double stopLng);
}
