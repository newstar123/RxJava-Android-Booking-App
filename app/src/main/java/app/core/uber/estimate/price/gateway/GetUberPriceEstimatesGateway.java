package app.core.uber.estimate.price.gateway;

import com.google.android.gms.maps.model.LatLng;

import app.core.uber.estimate.price.entity.UberPriceEstimatesResponse;
import rx.Observable;

public interface GetUberPriceEstimatesGateway {
    Observable<UberPriceEstimatesResponse> get(LatLng start, LatLng stop);
}
