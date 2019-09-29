package app.gateway.bars.menu;

import app.core.bars.menu.entity.BarMenuRestModel;
import app.core.bars.menu.gateway.GetBarMenuListGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetBarMenuRestGateway implements GetBarMenuListGateway {
    private final GetBarMenuGateway menuGateway;

    public GetBarMenuRestGateway() {
        menuGateway = QorumHttpClient.get().create(GetBarMenuGateway.class);
    }

    @Override public Observable<BarMenuRestModel> get(long barId) {
        return menuGateway.get(barId).subscribeOn(Schedulers.io());
    }
}
