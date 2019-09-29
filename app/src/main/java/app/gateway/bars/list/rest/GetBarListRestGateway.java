package app.gateway.bars.list.rest;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetBarListRestGateway implements GetBarListRetrofitGateway {
    private final GetBarListRetrofitGateway gateway;

    public GetBarListRestGateway() {
        gateway = QorumHttpClient.get().create(GetBarListRetrofitGateway.class);
    }

    @Override public Observable<List<BarModel>> get(double lat, double lng, int miles) {
        return gateway.get(lat, lng, miles).subscribeOn(Schedulers.io());
    }

}
