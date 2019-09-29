package app.gateway.bars.item;

import app.core.bars.detail.gateway.GetBarItemGateway;
import app.core.bars.list.get.entity.BarModel;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetBarItemRestGateway implements GetBarItemGateway {
    private final GetBarItemGateway gateway;

    public GetBarItemRestGateway() {
        gateway = QorumHttpClient.get().create(GetBarItemGateway.class);
    }

    @Override public Observable<BarModel> get(long id) {
        return gateway.get(id).subscribeOn(Schedulers.io());
    }

}
