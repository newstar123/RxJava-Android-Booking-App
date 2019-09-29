package app.gateway.facebook.mock;

import app.core.facebook.mock.entity.MockPhotosResponse;
import retrofit2.http.GET;
import rx.Observable;

public interface GetMockPhotosRetrofitGateway {
    @GET("v2/patron/friends-pictures") Observable<MockPhotosResponse> get();
}
