package app.core.uber.product.gateway;


import com.google.android.gms.maps.model.LatLng;

import app.core.uber.product.entity.UberProductsResponse;
import rx.Observable;

public interface GetUberProductsGateway {
    Observable<UberProductsResponse> get(LatLng start);
}
