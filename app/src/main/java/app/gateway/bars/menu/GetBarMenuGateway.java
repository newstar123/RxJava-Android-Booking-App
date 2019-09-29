package app.gateway.bars.menu;

import app.core.bars.menu.entity.BarMenuRestModel;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GetBarMenuGateway {
    @GET("v2/vendors/{id}/menu") Observable<BarMenuRestModel> get(@Path("id") long barId);
}
