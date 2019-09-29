package app.gateway.advert;


import app.core.advert.entity.AdvertResponse;
import app.core.advert.gateway.AdvertGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;

public class AdvertRestGateway implements AdvertGateway {
    AdvertGateway advertGateway;

    public AdvertRestGateway() {
        advertGateway = QorumHttpClient.get().create(AdvertGateway.class);
    }

    @Override public Observable<AdvertResponse> get() {
        return advertGateway.get();
    }
}
