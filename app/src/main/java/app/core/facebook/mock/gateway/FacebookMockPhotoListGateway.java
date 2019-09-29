package app.core.facebook.mock.gateway;

import app.core.facebook.mock.entity.MockPhotosResponse;
import rx.Observable;

public interface FacebookMockPhotoListGateway {
    Observable<MockPhotosResponse> get();
}
