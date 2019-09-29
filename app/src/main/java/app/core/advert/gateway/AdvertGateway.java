package app.core.advert.gateway;


import app.core.advert.entity.AdvertResponse;
import retrofit2.http.GET;
import rx.Observable;

public interface AdvertGateway {
    @GET("v2/adverts/ridesafe") Observable<AdvertResponse> get();
}
