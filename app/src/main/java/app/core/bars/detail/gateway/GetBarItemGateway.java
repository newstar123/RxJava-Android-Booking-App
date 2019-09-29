package app.core.bars.detail.gateway;

import app.core.bars.list.get.entity.BarModel;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GetBarItemGateway {
    @GET("v2/vendors/{id}") Observable<BarModel> get(@Path("id") long barId);
}
