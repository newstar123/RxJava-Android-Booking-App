package app.gateway.facebook.mock;

import app.core.facebook.mock.entity.MockPhotosResponse;
import app.core.facebook.mock.gateway.FacebookMockPhotoListGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;

public class GetMockPhotosRestGateway implements FacebookMockPhotoListGateway {

    private final GetMockPhotosRetrofitGateway gateway;

    public GetMockPhotosRestGateway() {
        gateway = QorumHttpClient.get().create(GetMockPhotosRetrofitGateway.class);
    }

    @Override public Observable<MockPhotosResponse> get() {
        return gateway.get();
    }
}
