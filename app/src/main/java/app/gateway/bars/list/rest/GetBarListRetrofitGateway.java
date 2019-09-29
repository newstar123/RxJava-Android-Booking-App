package app.gateway.bars.list.rest;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GetBarListRetrofitGateway {
    @GET("v2/vendors") Observable<List<BarModel>> get(@Query("lat") double lat,
                                                           @Query("lng") double lng,
                                                           @Query("range_miles") int miles);
}
